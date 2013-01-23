package de.uni_koblenz.jgralab.tools.grapheditor.properties;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.MapDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards.DomainEditorWizardProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.AttributeColumnLableProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.AttributeTableContentProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

/**
 * PropertySheetPage to display properties of GraphElements
 * 
 * @author kheckelmann
 *
 */
/**
 * @author kheckelmann
 * 
 */
public class GraphElementPropertySheetPage extends Page implements
		IPropertySheetPage {

	private GraphEditor graphEditor;
	private Composite container;
	private AttributedElement<?, ?> element;

	// ID and Type Class
	private Label idLabel;
	private Text idText;
	private Label acLabel;
	private Text acText;

	// Alpha and Omega of Edge
	private Composite edgeAlphaOmegaContainer;
	private Label alphaLabel;
	private Text alphaText;
	private Label omegaLabel;
	private Text omegaText;

	// Viewer for Attributes
	private TableViewer viewer;

	// Buttons for complex Domains in table
	private ArrayList<Button> editButtons = new ArrayList<Button>();

	/**
	 * Constructor
	 * 
	 * @param edit
	 */
	public GraphElementPropertySheetPage(GraphEditor edit) {
		super();
		this.graphEditor = edit;
	}

	/**
	 * Create the UI of the PropertySheetPage
	 */
	@Override
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NONE);

		// Layout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		this.container.setLayout(layout);

		// ID and Class
		this.createIDandClassFields();

		// Edge Alpha and Omega
		this.createAlphaAndOmegaFields();

		// Attributes
		this.createAttributeTable();

		// Initialize selection
		Object e = ((IStructuredSelection) this.graphEditor.getZoomableViewer()
				.getSelection()).getFirstElement();
		if (e == null) {
			this.element = (Graph) this.graphEditor.getZoomableViewer()
					.getInput();
		} else if (e instanceof GraphElement) {
			this.element = (GraphElement<?, ?>) e;
		} else {
			return;
		}
		this.update();

	}

	/**
	 * Create ID and EdgeClass, VertexClass or GraphClass labels and fields ID
	 * field is changeable only for Graphs
	 */
	private void createIDandClassFields() {
		Composite idVECcontainer = new Composite(this.container, SWT.NONE);
		GridLayout idVEClayout = new GridLayout();
		idVEClayout.numColumns = 2;
		idVECcontainer.setLayout(idVEClayout);
		idVECcontainer.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));

		this.idLabel = new Label(idVECcontainer, SWT.NONE);
		this.idText = new Text(idVECcontainer, SWT.BORDER | SWT.READ_ONLY);
		this.idText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
		this.acLabel = new Label(idVECcontainer, SWT.NONE);
		this.acText = new Text(idVECcontainer, SWT.BORDER | SWT.READ_ONLY);
		this.acText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
				false));
	}

	/**
	 * Create the alpha and omega labels and fields for an edge
	 */
	private void createAlphaAndOmegaFields() {
		this.edgeAlphaOmegaContainer = new Composite(this.container, SWT.NONE);
		GridLayout eACClayout = new GridLayout();
		eACClayout.numColumns = 4;
		this.edgeAlphaOmegaContainer.setLayout(eACClayout);
		this.edgeAlphaOmegaContainer.setLayoutData(new GridData(SWT.FILL,
				SWT.BEGINNING, true, false));

		this.alphaLabel = new Label(this.edgeAlphaOmegaContainer, SWT.NONE);
		this.alphaLabel.setText("Edge from  Alpha:");
		this.alphaText = new Text(this.edgeAlphaOmegaContainer, SWT.BORDER);
		this.alphaText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		this.alphaText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					GraphElementPropertySheetPage.this.updateAlphaOfEdge();
				}
			}
		});
		this.alphaText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				GraphElementPropertySheetPage.this.updateAlphaOfEdge();
			}
		});
		this.omegaLabel = new Label(this.edgeAlphaOmegaContainer, SWT.NONE);
		this.omegaLabel.setText(" to  Omega:");
		this.omegaText = new Text(this.edgeAlphaOmegaContainer, SWT.BORDER);
		this.omegaText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		this.omegaText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					GraphElementPropertySheetPage.this.updateOmegaOfEdge();
				}
			}
		});
		this.omegaText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				GraphElementPropertySheetPage.this.updateOmegaOfEdge();
			}
		});
	}

	/**
	 * Create the table with the attributes depending on the type
	 */
	private void createAttributeTable() {
		this.viewer = new TableViewer(this.container, SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		final Table table = this.viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.viewer.getControl().setLayoutData(gridData);

		this.viewer.setContentProvider(new AttributeTableContentProvider());
		this.createColumns();
	}

	private void createColumns() {
		TableViewerColumn col1 = new TableViewerColumn(this.viewer, SWT.NONE);
		col1.getColumn().setText("Attribute");
		col1.getColumn().setWidth(100);
		col1.getColumn().setResizable(true);
		col1.getColumn().setMoveable(false);
		col1.setLabelProvider(new AttributeColumnLableProvider(0));
		TableViewerColumn col2 = new TableViewerColumn(this.viewer, SWT.NONE);
		col2.getColumn().setWidth(200);
		col2.getColumn().setResizable(true);
		col2.getColumn().setMoveable(false);
		col2.getColumn().setText("Value");
		col2.setLabelProvider(new AttributeColumnLableProvider(1));
		TableViewerColumn col3 = new TableViewerColumn(this.viewer, SWT.NONE);
		col3.getColumn().setWidth(100);
		col3.getColumn().setResizable(true);
		col3.getColumn().setMoveable(false);
		col3.getColumn().setText("Type");
		col3.setLabelProvider(new AttributeColumnLableProvider(2));

		col2.setEditingSupport(new BasicDomainEditingSupport(this.viewer,
				this.graphEditor));
	}

	/**
	 * Update the contents with a new selected element
	 */
	private void update() {

		// Update viewer
		this.viewer.setInput(this.element);
		this.viewer.refresh();

		if (this.element instanceof Graph) {
			this.idLabel.setText("GraphID: ");
			this.acLabel.setText("GraphClass: ");
			this.idText.setText(((Graph) this.element).getId());
			this.acText
					.setText(this.element.getGraphClass().getQualifiedName());
			// Graph has no alpha and omega
			this.edgeAlphaOmegaContainer.setVisible(false);
		} else {

			// Update ID and AttributedElementClass
			if (this.element instanceof Vertex) {
				this.idLabel.setText("VertexID:");
				this.acLabel.setText("VertexClass:");
				// Vertex has no alpha and omega
				this.edgeAlphaOmegaContainer.setVisible(false);
			} else {
				this.idLabel.setText("EdgeID:  ");
				this.acLabel.setText("EdgeClass:  ");
				// Edge has alpha and omega
				this.edgeAlphaOmegaContainer.setVisible(true);
				this.alphaText.setText("v"
						+ ((Edge) this.element).getAlpha().getId());
				this.omegaText.setText("v"
						+ ((Edge) this.element).getOmega().getId());
			}
			this.idText.setText(((GraphElement<?, ?>) this.element).getId()
					+ "");
			this.acText.setText(this.element.getAttributedElementClass()
					.getQualifiedName());
		}

		// Update Button Column
		final Table table = this.viewer.getTable();
		TableItem[] items = table.getItems();
		// Iterate over all entrys in the table
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			AttributeTableRow row = (AttributeTableRow) this.viewer
					.getElementAt(i);
			Domain dom = row.attribute.getDomain();
			// if the domain is one that requires a complex editor, add a button
			// to open it
			if (dom instanceof CollectionDomain || dom instanceof MapDomain
					|| dom instanceof RecordDomain) {
				TableEditor editor = new TableEditor(table);
				Button button = new Button(table, SWT.PUSH);
				this.editButtons.add(button);
				button.setText("edit");
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.RIGHT;
				editor.setEditor(button, item, 1);
				button.setData(new Object[] { row.attribute, row.element });
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Object[] data = (Object[]) ((Button) e.getSource())
								.getData();
						Attribute attribute = (Attribute) data[0];
						AttributedElement<?, ?> element = (AttributedElement<?, ?>) data[1];
						Object valueToEdit = element.getAttribute(attribute
								.getName());

						// Open Editor and get Result
						Object editedValue = DomainEditorWizardProvider
								.getEditResult(attribute.getDomain(),
										valueToEdit);
						// Save it
						OperationProvider.doChangeAttributeOperation(element,
								attribute, editedValue,
								GraphElementPropertySheetPage.this.graphEditor,
								GraphElementPropertySheetPage.this.viewer);

					}
				});
			}
		}
	}

	/**
	 * Update the alpha end of an edge
	 */
	private void updateAlphaOfEdge() {
		try {
			Edge edge = (Edge) this.element;
			int alpha = (Integer
					.parseInt(this.alphaText.getText().substring(1)));
			if (alpha == edge.getAlpha().getId()) {
				return;
			}
			IStatus status = OperationProvider.doChangeAlphaOfEdgeOperation(
					edge, alpha, this.graphEditor);
			if (status.equals(Status.CANCEL_STATUS)) {
				this.alphaText.setText("v" + edge.getAlpha().getId());
			}
		} catch (NumberFormatException numex) {
			this.createErrorMessageBox(
					"Save failed!",
					"Edge source or target can not be parsed. "
							+ " Must be v with a following Integer, for example \"v1\".");
		}
	}

	/**
	 * Update the omega end of an edge
	 */
	private void updateOmegaOfEdge() {
		try {
			Edge edge = (Edge) this.element;
			int omega = (Integer
					.parseInt(this.omegaText.getText().substring(1)));
			if (omega == edge.getOmega().getId()) {
				return;
			}
			IStatus status = OperationProvider.doChangeOmegaOfEdgeOperation(
					edge, omega, this.graphEditor);
			if (status.equals(Status.CANCEL_STATUS)) {
				this.omegaText.setText("v" + edge.getOmega().getId());
			}
		} catch (NumberFormatException numex) {
			this.createErrorMessageBox(
					"Save failed!",
					"Edge source or target can not be parsed. "
							+ " Must be v with a following Integer, for example \"v1\".");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return this.container;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	@Override
	public void setFocus() {
		this.idText.setFocus();
	}

	/**
	 * Method called, when selection changed
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (this.viewer == null) {
			return;
		}
		Object e = ((IStructuredSelection) selection).getFirstElement();

		// if selection is empty, Graph is selected
		if (e == null) {
			for (Button b : this.editButtons) {
				b.dispose();
			}
			this.editButtons.clear();
			this.element = (Graph) this.graphEditor.getZoomableViewer()
					.getInput();
			this.update();
		}
		// if the selection is not empty it should be a selected GraphElement
		else if (e instanceof GraphElement) {
			for (Button b : this.editButtons) {
				b.dispose();
			}
			this.editButtons.clear();
			this.element = (GraphElement<?, ?>) e;
			this.update();
		}
	}

	/**
	 * Create an error message box with the given title and message
	 * 
	 * @param title
	 * @param message
	 */
	private void createErrorMessageBox(String title, String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		MessageBox mb = new MessageBox(shell, SWT.ERROR);
		mb.setText(title);
		mb.setMessage(message);
		mb.open();
	}
}
