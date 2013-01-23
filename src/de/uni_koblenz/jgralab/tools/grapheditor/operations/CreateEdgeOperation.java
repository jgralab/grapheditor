package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class CreateEdgeOperation extends AbstractOperation {

	private Graph graph;
	private EdgeClass edgeClass;
	private int alphaID;
	private int omegaID;
	private GraphEditor editor;
	private Edge createdEdge;

	public CreateEdgeOperation(Graph g, EdgeClass ec, Vertex alpha,
			Vertex omega, GraphEditor edit) {
		super("create edge");
		this.graph = g;
		this.edgeClass = ec;
		this.alphaID = alpha.getId();
		this.omegaID = omega.getId();
		this.editor = edit;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.createdEdge = this.graph.createEdge(this.edgeClass,
				this.graph.getVertex(this.alphaID),
				this.graph.getVertex(this.omegaID));
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.createdEdge = this.graph.createEdge(this.edgeClass,
				this.graph.getVertex(this.alphaID),
				this.graph.getVertex(this.omegaID));
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteEdge(this.createdEdge);
		this.createdEdge = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

}
