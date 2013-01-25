package de.uni_koblenz.jgralab.tools.grapheditor.editor;

import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.gef4.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.gef4.zest.layouts.LayoutAlgorithm;
import org.eclipse.gef4.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.exception.GraphIOException;
import de.uni_koblenz.jgralab.graphmarker.BitSetVertexMarker;
import de.uni_koblenz.jgralab.grumlschema.SchemaGraph;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.tools.grapheditor.filter.IncidenceFilter;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.JGraLabContentProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.JGraLabLabelProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.layout.algorithm.CircleLayoutAlgorithm;
import de.uni_koblenz.jgralab.tools.grapheditor.properties.GraphElementPropertySheetPage;
import de.uni_koblenz.jgralab.utilities.rsa2tg.Rsa2Tg;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.SchemaGraph2Schema;

public class GraphEditor extends EditorPart implements IZoomableWorkbenchPart {
	public static final String ID = "de.uni_koblenz.jgralab.tools.grapheditor.editor_main";

	/**
	 * Indicates unsaved changes
	 */
	private boolean dirty = false;

	/**
	 * GraphViewer that displays the Graph
	 */
	private GraphViewer viewer;

	/**
	 * Graph to edit
	 */
	private Graph graph;

	private ZoomContributionViewItem toolbarZoomContributionViewItem;

	/**
	 * Marker to pin Vertices in Filter Mode
	 */
	private BitSetVertexMarker pinMarker;

	/**
	 * Path to loaded graph file
	 */
	private String graphpath;

	/**
	 * Indicates whether the shown graph is new and not yet saved
	 */
	private boolean isNew;

	/**
	 * Context for Undo and Redo for this Editor
	 */
	private UndoContext context;

	/**
	 * PropertySheetPage for this Editor
	 */
	private GraphElementPropertySheetPage propPage = new GraphElementPropertySheetPage(
			this);

	/**
	 * Action Handlers for undo and redo, specific for each editor
	 */
	private UndoActionHandler undoActionHandler;
	private RedoActionHandler redoActionHandler;

	/**
	 * Listener that change the filter on selection
	 */
	private ISelectionChangedListener filterListener;

	/**
	 * Boolean indicating, whether the filter is active or not
	 */
	private boolean toggle = false;

	/**
	 * Current status line message for this editor
	 */
	private String statusLineMessage;

	/**
	 * Initializes the Editor
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		GraphEditorInput in = null;
		Schema schema = null;
		String schemaPath = "";
		try {
			if (input instanceof GraphEditorInput) {
				in = (GraphEditorInput) input;
				schemaPath = in.getPath();
				schema = this.getSchemaFromPath(schemaPath);
				this.isNew = true;
			} else if (input instanceof FileEditorInput) {
				FileEditorInput fileInput = (FileEditorInput) input;
				schemaPath = fileInput.getPath().toOSString();
				schema = GraphIO.loadSchemaFromFile(schemaPath);
				this.isNew = false;
			} else {
				throw new RuntimeException(input.getClass()
						+ " is not a GraphEditorInput");
			}
			schema.finish();

		} catch (GraphIOException e) {
			Shell shell = this.getEditorSite().getShell();
			MessageBox errormessage = new MessageBox(shell, SWT.ERROR);
			errormessage.setText("Error while loading schema");
			errormessage.setMessage("Can't load schema at " + schemaPath + "."
					+ "\n" + e.getMessage());
			errormessage.open();
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		this.setSite(site);
		this.setInput(input);
		this.createGlobalActionHandlers();

		this.propPage = new GraphElementPropertySheetPage(this);

		this.filterListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					if (((IStructuredSelection) selection).size() > 1) {
						return;
					}
					Object obj = ((IStructuredSelection) selection)
							.getFirstElement();
					if (obj != null) {
						GraphElement<?, ?> element = (GraphElement<?, ?>) obj;
						if (element instanceof Edge) {
							return;
						}
						GraphEditor.this.changeFilter((Vertex) element);
					}
				}
			}
		};

		if (this.isNew) {
			this.graph = this.createGraphFromSchema(schema, in.getGraphID());
			this.setDirty(true);
		} else {
			this.graph = this.loadGraph(schemaPath, schema);
		}
		if (this.graph == null) {
			throw new PartInitException(Status.CANCEL_STATUS);
		}

	}

	/**
	 * Returns a schema loaded from the given path. If it's an xmi file it is
	 * assumed that it's a schema exported from the RSA and the Rsa2Tg is
	 * applied.
	 * 
	 * @param schemaPath
	 *            the path to load the schema from
	 * @return the loaded schema
	 */
	private Schema getSchemaFromPath(String schemaPath)
			throws FileNotFoundException, XMLStreamException, GraphIOException {
		Schema schema;
		if (schemaPath.endsWith(".xmi")) {
			Rsa2Tg rsa2tg = new Rsa2Tg();
			rsa2tg.setUseNavigability(true);
			rsa2tg.process(schemaPath);
			SchemaGraph sg = rsa2tg.getSchemaGraph();
			SchemaGraph2Schema sg2s = new SchemaGraph2Schema();
			schema = sg2s.convert(sg);
		} else /* .tg */{
			schema = GraphIO.loadSchemaFromFile(schemaPath);
		}
		return schema;
	}

	/**
	 * Loads the Graph from the given path
	 * 
	 * @param path
	 *            location of the graph
	 * @param schema
	 */
	private Graph loadGraph(String path, Schema schema) {

		this.graphpath = path;

		try {
			Graph g = GraphIO.loadGraphFromFile(path, schema,
					ImplementationType.GENERIC, null);
			return g;

		} catch (GraphIOException e) {
			Shell shell = this.getEditorSite().getShell();
			MessageBox errormessage = new MessageBox(shell, SWT.ERROR);
			errormessage.setText("Error while loading Graph");
			errormessage.setMessage("Can't load graph at " + path + "." + "\n"
					+ e.getMessage());
			errormessage.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates a new Graph matching the Schema locating at pathToSchema
	 * 
	 * @param schema
	 * @param pathToSchema
	 *            the path of the schema
	 */
	private Graph createGraphFromSchema(Schema schema, String graphID) {
		Graph graph = schema.createGraph(ImplementationType.GENERIC, graphID,
				40, 50);
		return graph;
	}

	private void changeFilter(Vertex element) {
		this.setStatusLineMessage("");
		this.viewer.setFilters(new ViewerFilter[] { new IncidenceFilter(this,
				element, this.pinMarker) });
		this.viewer.applyLayout();
	}

	/**
	 * Setup the view
	 */
	@Override
	public void createPartControl(Composite parent) {

		// Setup the Graph Viewer
		this.viewer = new GraphViewer(parent, SWT.BORDER);
		this.viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		this.viewer.setContentProvider(new JGraLabContentProvider());

		this.getSite().setSelectionProvider(this.viewer);

		this.initGraphInViewer(this.graph);

		this.viewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						GraphEditor.this.refreshPropertyPage();
					}
				});

		this.fillToolBar();
	}

	/**
	 * Initializes the Graph. Input of GraphViewer is set, GraphEditor is
	 * opened.
	 * 
	 * @param graph
	 */
	private void initGraphInViewer(Graph graph) {

		this.pinMarker = new BitSetVertexMarker(graph);
		this.viewer.setLabelProvider(new JGraLabLabelProvider(this.pinMarker));

		// Name the EditorTab after the Graphs ID
		this.setPartName(graph.getId());

		// If the graph is too big, activate the filter
		if (graph.getVCount() > 100) {
			this.viewer.addSelectionChangedListener(this.filterListener);
			this.viewer.setFilters(new ViewerFilter[] { new IncidenceFilter(
					this, graph.getFirstVertex(), this.pinMarker) });
			this.viewer.setInput(graph);
			this.setLayout(new CircleLayoutAlgorithm());
			this.toggle = true;
		} else {
			this.viewer.setInput(graph);
			this.setLayout(new SpringLayoutAlgorithm());
		}
	}

	/**
	 * Adds a zoom function to the toolbar of this editor
	 */
	private void fillToolBar() {
		this.toolbarZoomContributionViewItem = new ZoomContributionViewItem(
				this);
	}

	/**
	 * Returns the GraphViewer
	 */
	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return this.viewer;
	}

	/**
	 * Sets a new LayoutAlgorithm to the GraphViewer
	 * 
	 * @param layout
	 */
	public void setLayout(LayoutAlgorithm layout) {
		this.viewer.getGraphControl().setDynamicLayout(false);
		this.viewer.setLayoutAlgorithm(layout, true);
		this.viewer.applyLayout();
		this.refresh();
	}

	/**
	 * Calls the refresh method of the GraphViewer
	 */
	public void refresh() {
		this.viewer.refresh();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		this.viewer.getGraphControl().setFocus();
		IActionBars actionBars = this.getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				this.undoActionHandler);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				this.redoActionHandler);
		actionBars.getToolBarManager().removeAll();
		actionBars.getToolBarManager()
				.add(this.toolbarZoomContributionViewItem);
	}

	/**
	 * Indicates whether the editor has unsaved changes
	 */
	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * Set the dirty property and fires a PropertyChange Event
	 * 
	 * @param dirty
	 *            whether there are unsaved changes in the editor
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	/**
	 * Saves the graph to file. If it is a new Graph, ask the user for the path.
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (this.isNew) {
			this.doSaveAs();
			this.isNew = false;
		} else {
			try {
				GraphIO.saveGraphToFile((Graph) this.viewer.getInput(),
						this.graphpath, null);
				this.setDirty(false);
			} catch (GraphIOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens a save dialog and proceeds the save
	 */
	@Override
	public void doSaveAs() {
		Shell shell = this.getSite().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*.tg" });
		fileDialog.setFilterNames(new String[] { "TG-files (*.tg)" });
		if (this.graphpath != null) {
			fileDialog.setFileName(this.graphpath.substring(this.graphpath
					.lastIndexOf("/") + 1));
		}
		String path = fileDialog.open();
		if (path == null) {
			return;
		}
		try {
			GraphIO.saveGraphToFile((Graph) this.viewer.getInput(), path, null);
			this.graphpath = path;
			this.setDirty(false);
		} catch (GraphIOException e) {
			MessageBox mb = new MessageBox(shell, SWT.ERROR);
			mb.setText("Error while saving Graph");
			mb.setMessage("Can't save graph to " + path + ".");
			mb.open();
		}
	}

	/**
	 * Calling SaveAs is allowed
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Crazy getter for IPropertySheetPage
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			return this.propPage;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Update the PropertySheetPage
	 */
	private void refreshPropertyPage() {
		IWorkbenchPage page = this.getEditorSite().getWorkbenchWindow()
				.getActivePage();
		IViewPart view = page.findView("org.eclipse.ui.views.PropertySheet");
		if (view != null && page.isPartVisible(view)) {
			this.propPage.selectionChanged(this, this.viewer.getSelection());
		}
	}

	/**
	 * @return the undo context of this editor
	 */
	public UndoContext getUndoContext() {
		return this.context;
	}

	/**
	 * Initialize undo and redo
	 */
	private void createGlobalActionHandlers() {
		this.context = new UndoContext();

		this.undoActionHandler = new UndoActionHandler(this.getEditorSite(),
				this.context);
		this.redoActionHandler = new RedoActionHandler(this.getEditorSite(),
				this.context);

		IActionBars actionBars = this.getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
				this.undoActionHandler);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
				this.redoActionHandler);

		new UndoRedoActionGroup(this.getEditorSite(), this.context, true);
	}

	/**
	 * @return whether the filter is active
	 */
	public boolean isFiltered() {
		return this.toggle;
	}

	/**
	 * Toggle the filter per editor
	 */
	public void toggleFilter() {
		if (this.toggle) {
			this.viewer.removeSelectionChangedListener(this.filterListener);
			this.viewer.setFilters(new ViewerFilter[] {});
			this.viewer.applyLayout();
			this.setStatusLineMessage("");
		} else {
			ISelection selection = this.viewer.getSelection();
			if (selection != null && selection instanceof IStructuredSelection) {
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj != null) {
					GraphElement<?, ?> element = (GraphElement<?, ?>) obj;
					if (element instanceof Edge) {
						return;
					}
					this.viewer
							.addSelectionChangedListener(this.filterListener);
					this.viewer
							.setFilters(new ViewerFilter[] { new IncidenceFilter(
									this, (Vertex) element, this.pinMarker) });

					this.setLayout(new CircleLayoutAlgorithm());
				} else {
					// don't toggle
					return;
				}
			} else {
				// don't toggle
				return;
			}
		}
		this.toggle = !this.toggle;
		return;
	}

	/**
	 * @return the BitSetVertexMarker that marks the pinned vertices
	 */
	public BitSetVertexMarker getPinMarker() {
		return this.pinMarker;
	}

	/**
	 * If this editor is disposed and it is the last editor, tell the source
	 * provider that no graph is loaded
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	public String getStatusLineMessage() {
		return this.statusLineMessage;
	}

	public void setStatusLineMessage(String statusLineMessage) {
		this.statusLineMessage = statusLineMessage;
		this.getEditorSite().getActionBars().getStatusLineManager()
				.setMessage(this.statusLineMessage);
	}

	public void refreshStatusLine() {
		this.getEditorSite().getActionBars().getStatusLineManager()
				.setMessage(this.statusLineMessage);
	}

}