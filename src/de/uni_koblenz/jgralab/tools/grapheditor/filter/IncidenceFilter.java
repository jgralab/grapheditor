package de.uni_koblenz.jgralab.tools.grapheditor.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.graphmarker.BitSetVertexMarker;

/**
 * Filter that shows only neighbours of selected vertex and pinned vertices
 * 
 * @author kheckelmann
 * 
 */
public class IncidenceFilter extends ViewerFilter {

	private final int maxIncidences = 20;

	private Set<Vertex> incidences;
	private BitSetVertexMarker marker;

	public IncidenceFilter(GraphEditor editor, Vertex selection,
			BitSetVertexMarker marker) {
		this.marker = marker;
		this.incidences = new HashSet<Vertex>();
		this.incidences.add(selection);
		for (Edge e : selection.incidences()) {
			this.incidences.add(e.getAlpha());
			this.incidences.add(e.getOmega());
			if (incidences.size() > maxIncidences) {
				editor.setStatusLineMessage("Vertex has too many Incidences. Only "
						+ maxIncidences
						+ " Edges from "
						+ selection.getDegree() + " are visible.");
				break;
			}
		}
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof Edge)
			return true;
		if (marker.isMarked((Vertex) element))
			return true;
		return this.incidences.contains(element);
	}

}
