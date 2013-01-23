package de.uni_koblenz.jgralab.tools.grapheditor.filter;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BitSetVertexMarker;

public class RadiusIncidenceFilter extends ViewerFilter {

	private Set<Vertex> incidences;
	private BitSetVertexMarker marker;
	
	public RadiusIncidenceFilter(Viewer viewer, Vertex selection, BitSetVertexMarker marker){
		this.marker = marker;
		this.incidences = new HashSet<Vertex>();
		this.incidences.add(selection);
		Set<Vertex> temp = getNeighbours(selection);
		this.incidences.addAll(temp);
		temp.remove(selection);
		for(Vertex t : temp){
			Set<Vertex> temp2 = getNeighbours(t);
			this.incidences.addAll(temp2);
		}	
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(element instanceof Edge) return true;	
		if(marker.isMarked((Vertex) element)) return true;
		return this.incidences.contains(element);
	}
	
	private Set<Vertex> getNeighbours(Vertex selection){
		Set<Vertex> n = new HashSet<Vertex>();
		n.add(selection);
		for(Edge e : selection.incidences()){
			n.add(e.getAlpha());
			n.add(e.getOmega());
		}
		return n;
	}
}
