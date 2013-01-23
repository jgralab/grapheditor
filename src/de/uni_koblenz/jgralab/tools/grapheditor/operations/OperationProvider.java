package de.uni_koblenz.jgralab.tools.grapheditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbench;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.VertexClass;

public class OperationProvider {


	public static void doChangeAttributeOperation(AttributedElement<?,?> ele, Attribute attribute, Object value, GraphEditor view, TableViewer viewer){
		IUndoableOperation operation = new ChangeAttributeOperation(ele, attribute, value, view, viewer);
		startOperation(view, operation);
	}

	public static void doCreateVertexOperation(Graph graph, VertexClass vertexClass, GraphEditor editor){
		IUndoableOperation operation = new CreateVertexOperation(graph, vertexClass,editor);
		startOperation(editor, operation);
	}
	
	public static void doCreateEdgeOperation(Graph graph, EdgeClass edgeClass, Vertex alpha, Vertex omega, GraphEditor editor){
		IUndoableOperation operation = new CreateEdgeOperation(graph, edgeClass, alpha, omega,editor);
		startOperation(editor, operation);
	}
	
	public static void doDeleteVertexOperation(Vertex vertex, GraphEditor editor){
		IUndoableOperation operation = new DeleteVertexOperation(vertex,  editor);
		startOperation(editor, operation);
	}
	
	public static void doDeleteEdgeOperation(Edge edge, GraphEditor editor){
		IUndoableOperation operation = new DeleteEdgeOperation(edge,  editor);
		startOperation(editor, operation);
	}
	
	public static IStatus doChangeAlphaOfEdgeOperation(Edge edge, int newVertexID, GraphEditor editor){
		IUndoableOperation operation = new ChangeEdgeEndOperation(edge, newVertexID, true, editor);
		return startOperation(editor, operation);
	}
	
	public static IStatus doChangeOmegaOfEdgeOperation(Edge edge, int newVertexID, GraphEditor editor){
		IUndoableOperation operation = new ChangeEdgeEndOperation(edge, newVertexID, false, editor);
		return startOperation(editor, operation);
	}
	
	private static IStatus startOperation(GraphEditor editor,
			IUndoableOperation operation) {
		IWorkbench workbench = editor.getSite().getWorkbenchWindow().getWorkbench();
		IOperationHistory operationHistory = workbench.getOperationSupport().getOperationHistory();
		IUndoContext undoContext = editor.getUndoContext();
		operation.addContext(undoContext);
		try {
			return operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
		
}
