package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.exception.GraphException;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class ChangeEdgeEndOperation extends AbstractOperation {

	private int id;
	private boolean isAlpha;
	private int idOldVertex;
	private int idNewVertex;
	private Graph graph;
	private GraphEditor editor;

	public ChangeEdgeEndOperation(Edge edge, int newVertexID, boolean isAlpha,
			GraphEditor editor) {
		super("change edge end");
		this.id = edge.getId();
		this.isAlpha = isAlpha;
		this.idOldVertex = this.isAlpha ? edge.getAlpha().getId() : edge
				.getOmega().getId();
		this.idNewVertex = newVertexID;
		this.graph = edge.getGraph();
		this.editor = editor;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return this.setValue(this.idNewVertex);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return this.setValue(this.idNewVertex);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return this.setValue(this.idOldVertex);
	}

	private IStatus setValue(int vertexID) {
		try {
			if (this.isAlpha) {
				this.graph.getEdge(this.id).setAlpha(
						this.graph.getVertex(vertexID));
			} else {
				this.graph.getEdge(this.id).setOmega(
						this.graph.getVertex(vertexID));
			}
			this.editor.setDirty(true);
			this.editor.refresh();
			return Status.OK_STATUS;
		} catch (GraphException grex) {
			this.createErrorMessageBox("Changing edge failed!",
					"Can not set edge source or target because changed value is invalid.");
			return Status.CANCEL_STATUS;
		} catch (NullPointerException nex) {
			this.createErrorMessageBox("Changing Edge failed!",
					"Can not found edge, source vertex or target vertex in graph");
			return Status.CANCEL_STATUS;
		}
	}

	private void createErrorMessageBox(String text, String message) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		MessageBox mb = new MessageBox(shell, SWT.ERROR);
		mb.setText(text);
		mb.setMessage(message);
		mb.open();
	}
}
