package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.zest.core.viewers.GraphViewer;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.operations.OperationProvider;

public class DeleteGraphElementsAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);

		GraphViewer graphViewer = (GraphViewer) editor.getZoomableViewer();
		Object[] selections = ((IStructuredSelection) graphViewer
				.getSelection()).toArray();
		for (int i = 0; i < selections.length; i++) {
			GraphElement<?, ?> element = (GraphElement<?, ?>) selections[i];
			if (element instanceof Edge) {
				OperationProvider.doDeleteEdgeOperation((Edge) element, editor);
			} else {
				OperationProvider.doDeleteVertexOperation((Vertex) element,
						editor);
			}
		}
		return null;
	}

}
