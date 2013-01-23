package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class CreateVertexOperation extends AbstractOperation {

	private Graph graph;
	private VertexClass vertexClass;
	private GraphEditor editor;
	private Vertex createdVertex;

	public CreateVertexOperation(Graph g, VertexClass vc, GraphEditor edit) {
		super("create vertex");
		this.graph = g;
		this.vertexClass = vc;
		this.editor = edit;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.createdVertex = this.graph.createVertex(this.vertexClass);
		if (this.editor.isFiltered()) {
			this.editor.getPinMarker().mark(this.createdVertex);
		}
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.createdVertex = this.graph.createVertex(this.vertexClass);

		if (this.editor.isFiltered()) {
			this.editor.getPinMarker().mark(this.createdVertex);
		}
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteVertex(this.createdVertex);
		this.createdVertex = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

}
