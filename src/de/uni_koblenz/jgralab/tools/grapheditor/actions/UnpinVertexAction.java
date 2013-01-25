package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class UnpinVertexAction extends AbstractHandler {

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
			if (element instanceof Vertex) {
				editor.getPinMarker().removeMark((Vertex) element);
				graphViewer.refresh(element);
			}
		}

		return null;
	}

}
