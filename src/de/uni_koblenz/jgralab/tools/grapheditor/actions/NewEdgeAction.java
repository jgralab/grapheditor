package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

public class NewEdgeAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);
		Shell shell = editor.getEditorSite().getShell();
		Graph graph = (Graph) editor.getZoomableViewer().getInput();

		// Ask which EdgeClass to create, show only concrete classes
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, new LabelProvider());
		dialog.setElements(this.filterAbstractClasses(
				graph.getSchema().getGraphClass().getEdgeClasses()).toArray());
		dialog.setTitle("Choose an EdgeClass");
		if (dialog.open() != Window.OK) {
			return null;
		}
		Object[] chosenEntries = dialog.getResult();
		EdgeClass ec = ((EdgeClass) chosenEntries[0]);

		// Find the possible alphas
		ArrayList<Vertex> list = new ArrayList<Vertex>();
		for (Vertex ver : graph.vertices(ec.getFrom().getVertexClass())) {
			list.add(ver);
		}

		// Ask which alpha to choose
		dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		dialog.setElements(list.toArray());
		dialog.setTitle("Choose an alpha");
		if (dialog.open() != Window.OK) {
			return null;
		}
		chosenEntries = dialog.getResult();
		Vertex alpha = (Vertex) chosenEntries[0];

		// Find the possible omegas
		list = new ArrayList<Vertex>();
		for (Vertex ver : graph.vertices(ec.getTo().getVertexClass())) {
			list.add(ver);
		}

		// Ask which omega to choose
		dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		dialog.setElements(list.toArray());
		dialog.setTitle("Choose an omega");
		if (dialog.open() != Window.OK) {
			return null;
		}
		chosenEntries = dialog.getResult();
		Vertex omega = (Vertex) chosenEntries[0];

		// Create the edge
		OperationProvider
				.doCreateEdgeOperation(graph, ec, alpha, omega, editor);

		return null;
	}

	private List<EdgeClass> filterAbstractClasses(List<EdgeClass> origList) {
		ArrayList<EdgeClass> newList = new ArrayList<EdgeClass>();
		for (EdgeClass vc : origList) {
			if (!vc.isAbstract()) {
				newList.add(vc);
			}
		}
		return newList;
	}

}
