package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import java.util.HashMap;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class DeleteEdgeOperation extends AbstractOperation {

	private Graph graph;
	private Edge edgeToDelete;
	private EdgeClass ec;
	private HashMap<String, Object> attributes;
	private int alphaID;
	private int omegaID;
	private GraphEditor editor;

	public DeleteEdgeOperation(Edge e, GraphEditor edit) {
		super("delete edge");
		this.graph = e.getGraph();
		this.edgeToDelete = e;
		this.alphaID = e.getAlpha().getId();
		this.omegaID = e.getOmega().getId();
		this.ec = e.getAttributedElementClass();
		this.editor = edit;
		this.attributes = new HashMap<String, Object>();
		for (Attribute a : e.getAttributedElementClass().getAttributeList()) {
			this.attributes.put(a.getName(),
					this.edgeToDelete.getAttribute(a.getName()));
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteEdge(this.edgeToDelete);
		this.edgeToDelete = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.graph.deleteEdge(this.edgeToDelete);
		this.edgeToDelete = null;
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		this.edgeToDelete = this.graph.createEdge(this.ec,
				this.graph.getVertex(this.alphaID),
				this.graph.getVertex(this.omegaID));
		for (String name : this.attributes.keySet()) {
			this.edgeToDelete.setAttribute(name, this.attributes.get(name));
		}
		this.editor.setDirty(true);
		this.editor.refresh();
		return Status.OK_STATUS;
	}

}
