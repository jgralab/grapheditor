package de.uni_koblenz.jgralab.tools.grapheditor.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;
import de.uni_koblenz.jgralab.utilities.tg2dot.dot.GraphVizOutputFormat;

public class Export2DotAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!(HandlerUtil.getActiveEditor(event) instanceof GraphEditor)) {
			return null;
		}
		GraphEditor editor = (GraphEditor) HandlerUtil.getActiveEditor(event);

		Graph graph = (Graph) editor.getZoomableViewer().getInput();

		Shell shell = editor.getSite().getShell();
		FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
		fileDialog.setFilterExtensions(new String[] { "*.dot", "*.png", ".pdf",
				".gif" });
		fileDialog
				.setFilterNames(new String[] { "Dot-files (*.dot)",
						"Png-files (*.png)", "Pdf-files (*.pdf)",
						"Gif-files (*.gif)" });
		fileDialog.setFileName("graph.dot");
		String path = fileDialog.open();
		if (path == null || path.equals("")) {
			return null;
		}

		GraphVizOutputFormat format;
		if (path.endsWith(".png")) {
			format = GraphVizOutputFormat.PNG;
		} else if (path.endsWith(".pdf")) {
			format = GraphVizOutputFormat.PDF;
		} else if (path.endsWith(".gif")) {
			format = GraphVizOutputFormat.GIF;
		} else {
			format = GraphVizOutputFormat.DOT;
		}

		try {
			Tg2Dot.convertGraph(graph, path, format);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
