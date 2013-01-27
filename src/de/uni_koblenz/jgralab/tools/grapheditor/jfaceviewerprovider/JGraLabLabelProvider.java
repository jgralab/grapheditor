package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.BitSetVertexMarker;
import de.uni_koblenz.jgralab.schema.Attribute;

/**
 * Label provider for a GraphViewer with a JGraLab Graph as input
 * 
 * @author kheckelmann
 * 
 */
public class JGraLabLabelProvider extends LabelProvider implements
		IEntityStyleProvider {

	private BitSetVertexMarker pinMarker;

	public JGraLabLabelProvider(BitSetVertexMarker pinMarker) {
		this.pinMarker = pinMarker;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Vertex) {
			Vertex vertex = (Vertex) element;
			String label = "v" + vertex.getId() + ": "
					+ vertex.getAttributedElementClass().getQualifiedName();
			PVector<Attribute> attributes = (PVector<Attribute>) vertex
					.getAttributedElementClass().getAttributeList();
			for (Attribute attr : attributes) {
				if (vertex.getAttribute(attr.getName()) != null) {
					label += "\n"
							+ attr.getName()
							+ ": "
							+ this.getStringRepresentation(vertex
									.getAttribute(attr.getName()));// vertex.getAttribute(attr.getName()).toString();
				}
			}
			return label;
		} else if (element instanceof Edge) {
			Edge edge = (Edge) element;
			String label = "e" + edge.getId() + ": "
					+ edge.getAttributedElementClass().getQualifiedName();
			PVector<Attribute> attributes = (PVector<Attribute>) edge
					.getAttributedElementClass().getAttributeList();
			for (Attribute attr : attributes) {
				if (edge.getAttribute(attr.getName()) != null) {
					label += "\n"
							+ attr.getName()
							+ ": "
							+ this.getStringRepresentation(edge
									.getAttribute(attr.getName()));// edge.getAttribute(attr.getName()).toString();
				}
			}
			return label;
		} else if (element instanceof EntityConnectionData) {
			return "";
		}
		throw new RuntimeException("Wrong type: "
				+ element.getClass().toString());

	}

	private String getStringRepresentation(Object o) {
		if (o instanceof Record) {
			Record rec = (Record) o;
			return rec.toPMap().toString();
		} else {
			return o.toString();
		}
	}

	@Override
	public Color getNodeHighlightColor(Object entity) {
		return null;
	}

	@Override
	public Color getBorderColor(Object entity) {
		if (entity instanceof Vertex) {
			Vertex v = (Vertex) entity;
			if (this.pinMarker.isMarked(v)) {
				return PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_RED);
			}
		}
		return null;
	}

	@Override
	public Color getBorderHighlightColor(Object entity) {
		if (entity instanceof Vertex) {
			Vertex v = (Vertex) entity;
			if (this.pinMarker.isMarked(v)) {
				return PlatformUI.getWorkbench().getDisplay()
						.getSystemColor(SWT.COLOR_RED);
			}
		}
		return null;
	}

	@Override
	public int getBorderWidth(Object entity) {
		return -1;
	}

	@Override
	public Color getBackgroundColour(Object entity) {
		return null;
	}

	@Override
	public Color getForegroundColour(Object entity) {
		return null;
	}

	@Override
	public IFigure getTooltip(Object entity) {
		return null;
	}

	@Override
	public boolean fisheyeNode(Object entity) {
		return false;
	}
}
