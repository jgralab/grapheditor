package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import java.util.HashMap;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class DeleteVertexOperation extends AbstractOperation {

	private class FlatEdge {
		public int targetID;
		public boolean isTarget;
		public EdgeClass ec;
		public HashMap<String, Object> edgeAttributes = new HashMap<String, Object>();
	}

	private Graph graph;
	private Vertex vertexToDelete;
	private VertexClass vertexClass;
	private HashMap<String, Object> attributes;
	private Vector<FlatEdge> edges;
	private GraphEditor editor;

	public DeleteVertexOperation(Vertex v, GraphEditor edit) {
		super("delete vertex");
		this.graph = v.getGraph();
		this.vertexToDelete = v;
		this.vertexClass = v.getAttributedElementClass();
		this.editor = edit;
		this.attributes = new HashMap<String, Object>();
		for (Attribute a : this.vertexClass.getAttributeList()) {
			this.attributes.put(a.getName(), v.getAttribute(a.getName()));
		}
		this.edges = new Vector<DeleteVertexOperation.FlatEdge>();
		for (Edge e : v.incidences(EdgeDirection.OUT)) {
			FlatEdge fe = new FlatEdge();
			fe.targetID = e.getOmega().getId();
			fe.isTarget = false;
			fe.ec = e.getAttributedElementClass();
			for (Attribute a : fe.ec.getAttributeList()) {
				fe.edgeAttributes.put(a.getName(), e.getAttribute(a.getName()));
			}
			this.edges.add(fe);
		}
		for (Edge e : v.incidences(EdgeDirection.IN)) {
			FlatEdge fe = new FlatEdge();
			fe.targetID = e.getAlpha().getId();
			fe.isTarget = true;
			fe.ec = e.getAttributedElementClass();
			for (Attribute a : fe.ec.getAttributeList()) {
				fe.edgeAttributes.put(a.getName(), e.getAttribute(a.getName()));
			}
			this.edges.add(fe);
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteVertex(this.vertexToDelete);
		this.vertexToDelete = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteVertex(this.vertexToDelete);
		this.vertexToDelete = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.vertexToDelete = this.graph.createVertex(this.vertexClass);
		for (String atname : this.attributes.keySet()) {
			this.vertexToDelete.setAttribute(atname,
					this.attributes.get(atname));
		}
		for (FlatEdge fe : this.edges) {
			Edge e;
			if (fe.isTarget) {
				e = this.graph.createEdge(fe.ec,
						this.graph.getVertex(fe.targetID), this.vertexToDelete);
			} else {
				e = this.graph.createEdge(fe.ec, this.vertexToDelete,
						this.graph.getVertex(fe.targetID));
			}
			for (String atname : fe.edgeAttributes.keySet()) {
				e.setAttribute(atname, fe.edgeAttributes.get(atname));
			}
		}
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

}
