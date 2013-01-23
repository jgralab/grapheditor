package de.uni_koblenz.jgralab.tools.grapheditor.commands.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Command handler doing nothing
 * 
 * @author kheckelmann
 *
 */
public class DoNothingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}

}
