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
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

public class NewVertexAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);
		Shell shell = editor.getEditorSite().getShell();
		Graph graph = (Graph) editor.getZoomableViewer().getInput();

		// Ask which VertexClass to create
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, new LabelProvider());

		dialog.setElements(this.filterAbstractClasses(
				graph.getSchema().getGraphClass().getVertexClasses()).toArray());
		dialog.setTitle("Choose a VertexClass");
		if (dialog.open() != Window.OK) {
			return null;
		}
		Object[] result = dialog.getResult();

		// Create the Vertex
		OperationProvider.doCreateVertexOperation(graph,
				(VertexClass) result[0], editor);
		return null;
	}

	private List<VertexClass> filterAbstractClasses(List<VertexClass> origList) {
		ArrayList<VertexClass> newList = new ArrayList<VertexClass>();
		for (VertexClass vc : origList) {
			if (!vc.isAbstract()) {
				newList.add(vc);
			}
		}
		return newList;
	}
}
