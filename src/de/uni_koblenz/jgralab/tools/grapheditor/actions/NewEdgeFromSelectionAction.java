package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

public class NewEdgeFromSelectionAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);
		Shell shell = editor.getSite().getShell();

		Graph graph = (Graph) editor.getZoomableViewer().getInput();

		// Get the selection
		GraphViewer viewer = (GraphViewer) editor.getZoomableViewer();
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Object[] selectedElements = selection.toArray();

		// Selection not valid
		if (selectedElements.length > 2 || selectedElements.length < 1) {
			this.showMessageBox(
					shell,
					"Select 1 Vertex to create an Edge to itself and 2 Vertices to create an Edge between them.");
			return null;
		}
		// Selection of one element
		else if (selectedElements.length == 1) {
			if (!(selectedElements[0] instanceof Vertex)) {
				this.showMessageBox(shell,
						"The selected element is not a Vertex.");
				return null;
			}
			// declarations
			Set<EdgeClass> possibleEdgeClasses = new HashSet<EdgeClass>();
			Vertex vertex = (Vertex) selectedElements[0];
			VertexClass vc = vertex.getAttributedElementClass();
			Set<VertexClass> vcSuperClasses = new HashSet<VertexClass>();
			vcSuperClasses.add(vc);
			vcSuperClasses.addAll(vc.getAllSuperClasses());

			// collect possible EdgeClasses
			for (EdgeClass ec : vc.getConnectedEdgeClasses()) {
				if (ec.isAbstract()) {
					continue;
				}
				if (vcSuperClasses.contains(ec.getFrom().getVertexClass())) {
					if (vcSuperClasses.contains(ec.getTo().getVertexClass())) {
						possibleEdgeClasses.add(ec);
					}
				}
			}
			// Ask which EdgeClass to create
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					shell, new LabelProvider());
			dialog.setElements(possibleEdgeClasses.toArray());
			dialog.setTitle("Choose an EdgeClass");
			if (dialog.open() != Window.OK) {
				return null;
			}
			Object[] result = dialog.getResult();
			EdgeClass ec = ((EdgeClass) result[0]);
			OperationProvider.doCreateEdgeOperation(graph, ec, vertex, vertex,
					editor);
			editor.setDirty(true);
		}
		// Selection of two elements
		else/* selectedElements.length == 2 */{
			if (!(selectedElements[0] instanceof Vertex)
					|| !(selectedElements[1] instanceof Vertex)) {
				this.showMessageBox(shell,
						"One of the selected elements is not a Vertex.");
				return null;
			}
			// declarations
			Vertex vertex1 = (Vertex) selectedElements[0];
			Vertex vertex2 = (Vertex) selectedElements[1];
			VertexClass vc1 = vertex1.getAttributedElementClass();
			VertexClass vc2 = vertex2.getAttributedElementClass();
			Set<VertexClass> vc1SuperClasses = new HashSet<VertexClass>();
			vc1SuperClasses.add(vc1);
			vc1SuperClasses.addAll(vc1.getAllSuperClasses());
			Set<VertexClass> vc2SuperClasses = new HashSet<VertexClass>();
			vc2SuperClasses.add(vc2);
			vc2SuperClasses.addAll(vc2.getAllSuperClasses());
			Set<EdgeClass> posEC = new HashSet<EdgeClass>();

			// collect possible EdgeClasses
			for (EdgeClass ec : vc1.getConnectedEdgeClasses()) {
				if (ec.isAbstract()) {
					continue;
				}
				VertexClass from = ec.getFrom().getVertexClass();
				VertexClass to = ec.getTo().getVertexClass();
				if (vc1SuperClasses.contains(from)
						&& vc2SuperClasses.contains(to)) {
					posEC.add(ec);
				} else if (vc1SuperClasses.contains(to)
						&& vc2SuperClasses.contains(from)) {
					posEC.add(ec);
				}
			}

			// Ask which EdgeClass to create
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(
					shell, new LabelProvider());
			dialog.setElements(posEC.toArray());
			dialog.setTitle("Choose an EdgeClass");
			if (dialog.open() != Window.OK) {
				return null;
			}
			Object[] chosenEntries = dialog.getResult();
			EdgeClass ec = ((EdgeClass) chosenEntries[0]);

			if (vc1SuperClasses.contains(ec.getFrom().getVertexClass())) {
				OperationProvider.doCreateEdgeOperation(graph, ec, vertex1,
						vertex2, editor);
			} else {
				OperationProvider.doCreateEdgeOperation(graph, ec, vertex2,
						vertex1, editor);
			}
		}
		return null;
	}

	private void showMessageBox(Shell shell, String message) {
		MessageBox mb = new MessageBox(shell);
		mb.setText("Can not create Edge from selection");
		mb.setMessage(message);
		mb.open();
	}

}
