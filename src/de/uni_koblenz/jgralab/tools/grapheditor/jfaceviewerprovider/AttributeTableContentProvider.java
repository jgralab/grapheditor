package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.schema.Attribute;
import de.uni_koblenz.jgralab.tools.grapheditor.properties.AttributeTableRow;

/**
 * Content provider for Attribute table
 * 
 * @author kheckelmann
 * 
 */
public class AttributeTableContentProvider implements
		IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/**
	 * Returns a list of tuples of Attribute and AttributedElement
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof AttributedElement) {
			AttributedElement<?, ?> element = (AttributedElement<?, ?>) inputElement;
			ArrayList<AttributeTableRow> list = new ArrayList<AttributeTableRow>();
			for (Attribute attribute : element.getAttributedElementClass()
					.getAttributeList()) {
				list.add(new AttributeTableRow(attribute, element));
			}
			return list.toArray();
		}
		return null;
	}

}
