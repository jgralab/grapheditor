package de.uni_koblenz.jgralab.tools.grapheditor.commands;

import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.filter.IncidenceFilter;

/**
 * Text field in toolbar to select a Vertex manually by entering its ID
 * 
 * @author kheckelmann
 * 
 */
public class SelectVertex extends WorkbenchWindowControlContribution {

	public static String ID = "de.uni_koblenz.jgralab.tools.grapheditor.commands.select_vertex_field";

	public SelectVertex() {
		super("Select Vertex");
	}

	/**
	 * Create the text field and add selection behavior
	 */
	@Override
	protected Control createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 2;
		comp.setLayout(layout);
		Label label = new Label(comp, SWT.NONE);
		label.setText("Go to Vertex: ");
		Text text = new Text(comp, SWT.BORDER);
		text.addFocusListener(new FocusAdapter() {
			/**
			 * Clear status line if the text field lost focus
			 */
			@Override
			public void focusLost(FocusEvent e) {
				IEditorPart editor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (editor != null && editor instanceof GraphEditor) {
					SelectVertex.this
							.clearErrorMessageOnStatusLine((GraphEditor) editor);
				}
			}
		});
		text.addKeyListener(new KeyAdapter() {
			/**
			 * Select the specified vertex on enter
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				IEditorPart editor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor();
				if (editor instanceof GraphEditor) {
					GraphEditor graphEditor = (GraphEditor) editor;
					SelectVertex.this
							.clearErrorMessageOnStatusLine(graphEditor);
					if (e.keyCode != SWT.CR) {
						return;
					}
					try {
						GraphViewer vi = (GraphViewer) graphEditor
								.getZoomableViewer();
						int vid = Integer.parseInt(((Text) e.getSource())
								.getText());
						Graph graph = (Graph) graphEditor.getZoomableViewer()
								.getInput();
						Vertex vertex = graph.getVertex(vid);
						if (vertex == null) {
							SelectVertex.this.writeErrorMessageToStatusLine(
									graphEditor, ((Text) e.widget).getText()
											+ e.character);
							return;
						}
						if (graphEditor.isFiltered()) {
							graphEditor.setStatusLineMessage("");
							vi.setFilters(new ViewerFilter[] { new IncidenceFilter(
									graphEditor, vertex, graphEditor
											.getPinMarker()) });
						}
						StructuredSelection s = new StructuredSelection(vertex);
						vi.setSelection(s);
						vi.applyLayout();
					} catch (NumberFormatException nex) {
						SelectVertex.this.writeErrorMessageToStatusLine(
								graphEditor, ((Text) e.widget).getText()
										+ e.character);
					}
				}
			}
		});
		return comp;
	}

	/**
	 * Writes Error Message to status line
	 * 
	 * @param editor
	 * @param wrongID
	 */
	private void writeErrorMessageToStatusLine(GraphEditor editor,
			String wrongID) {
		editor.getEditorSite()
				.getActionBars()
				.getStatusLineManager()
				.setErrorMessage(
						"Can not found a Vertex matching the ID " + wrongID);
	}

	/**
	 * Clears status line
	 * 
	 * @param editor
	 */
	private void clearErrorMessageOnStatusLine(GraphEditor editor) {
		editor.getEditorSite().getActionBars().getStatusLineManager()
				.setErrorMessage(null);

	}

}
