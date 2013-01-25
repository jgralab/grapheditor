package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import java.util.ArrayList;

import org.eclipse.gef4.zest.core.viewers.IGraphEntityRelationshipContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;

/**
 * Content provider for a GraphViewer, works with a JGraLab Graph as input
 * element
 * 
 * @author kheckelmann
 * 
 */
public class JGraLabContentProvider implements
		IGraphEntityRelationshipContentProvider {

	/**
	 * Elements are the JGraLab vertices
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof Graph) {
			Graph g = (Graph) inputElement;
			ArrayList<Vertex> vertices = new ArrayList<Vertex>();
			for (Vertex v : g.vertices()) {
				vertices.add(v);
			}
			return vertices.toArray();
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * Relationships are the JGraLab edges
	 */
	@Override
	public Object[] getRelationships(Object source, Object dest) {

		if (source instanceof Vertex && dest instanceof Vertex) {
			Vertex srcvertex = (Vertex) source;
			Vertex dstvertex = (Vertex) dest;
			ArrayList<Edge> edges = new ArrayList<Edge>();
			for (Edge e : srcvertex.incidences(EdgeDirection.OUT)) {
				if (e.getOmega().equals(dstvertex)) {
					edges.add(e);
				}
			}
			return edges.toArray();
		}

		throw new RuntimeException("Wrong type");
	}

}
