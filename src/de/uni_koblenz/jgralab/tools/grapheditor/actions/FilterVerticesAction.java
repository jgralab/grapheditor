package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class FilterVerticesAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);
		editor.toggleFilter();
		return null;
	}

}
