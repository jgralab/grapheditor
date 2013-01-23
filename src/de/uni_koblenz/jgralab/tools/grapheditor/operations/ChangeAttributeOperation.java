package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TableViewer;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

public class ChangeAttributeOperation extends AbstractOperation {

	private enum ElementType {
		GRAPH, EDGE, VERTEX
	}

	private int id;
	private ElementType type;
	private Graph graph;
	private String attributeName;
	private Object oldValue;
	private Object newValue;
	private GraphEditor editor;
	private TableViewer tableViewer;

	public ChangeAttributeOperation(AttributedElement<?, ?> element,
			Attribute attribute, Object newValue, GraphEditor edit,
			TableViewer tv) {
		super("change attribute");
		this.attributeName = attribute.getName();
		this.oldValue = element.getAttribute(attribute.getName());
		this.newValue = newValue;
		this.editor = edit;
		this.tableViewer = tv;
		if (element instanceof GraphElement) {
			this.graph = ((GraphElement<?, ?>) element).getGraph();
			this.id = ((GraphElement<?, ?>) element).getId();
			if (element instanceof Vertex) {
				this.type = ElementType.VERTEX;
			} else {
				this.type = ElementType.EDGE;
			}
		} else {
			this.graph = (Graph) element;
			this.type = ElementType.GRAPH;
		}
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (this.type.equals(ElementType.VERTEX)) {
			this.graph.getVertex(this.id).setAttribute(this.attributeName,
					this.newValue);
		} else if (this.type.equals(ElementType.EDGE)) {
			this.graph.getEdge(this.id).setAttribute(this.attributeName,
					this.newValue);
		} else {
			this.graph.setAttribute(this.attributeName, this.newValue);
		}
		this.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (this.type.equals(ElementType.VERTEX)) {
			this.graph.getVertex(this.id).setAttribute(this.attributeName,
					this.newValue);
		} else if (this.type.equals(ElementType.EDGE)) {
			this.graph.getEdge(this.id).setAttribute(this.attributeName,
					this.newValue);
		} else {
			this.graph.setAttribute(this.attributeName, this.newValue);
		}
		this.refresh();
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (this.type.equals(ElementType.VERTEX)) {
			this.graph.getVertex(this.id).setAttribute(this.attributeName,
					this.oldValue);
		} else if (this.type.equals(ElementType.EDGE)) {
			this.graph.getEdge(this.id).setAttribute(this.attributeName,
					this.oldValue);
		} else {
			this.graph.setAttribute(this.attributeName, this.oldValue);
		}
		this.refresh();
		return Status.OK_STATUS;
	}

	private void refresh() {
		this.editor.setDirty(true);
		this.editor.refresh();
		this.tableViewer.refresh();
	}
}
