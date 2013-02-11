package de.uni_koblenz.jgralab.tools.grapheditor.commands.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShiftAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.layout.algorithm.CircleLayoutAlgorithm;

/**
 * Command handler that changes layout depending on given parameter
 * 
 * @author kheckelmann
 * 
 */
public class ChangeLayoutAlgorithmHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);

		String algoName = event
				.getParameter("de.uni_koblenz.jgralab.tools.grapheditor.parameter.select_layout");
		if (algoName.equals("c")) {
			editor.setLayout(new CircleLayoutAlgorithm());
		} else if (algoName.equals("g")) {
			editor.setLayout(new GridLayoutAlgorithm());
		} else if (algoName.equals("h")) {
			editor.setLayout(new HorizontalShiftAlgorithm());
		} else if (algoName.equals("r")) {
			editor.setLayout(new RadialLayoutAlgorithm());
		} else if (algoName.equals("s")) {
			editor.setLayout(new SpringLayoutAlgorithm());
		} else if (algoName.equals("t")) {
			editor.setLayout(new TreeLayoutAlgorithm());
		} else if (algoName.equals("d")) {
			editor.setLayout(new DirectedGraphLayoutAlgorithm());
		} else if (algoName.equals("p")) {
			editor.setLayout(new SpaceTreeLayoutAlgorithm());
		}

		return null;
	}

}
