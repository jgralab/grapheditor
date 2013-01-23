package de.uni_koblenz.jgralab.tools.grapheditor.properties;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.schema.Attribute;

/**
 * Structure class for the Attribute content provider
 * 
 * @author kheckelmann
 *
 */
public class AttributeTableRow {

	public Attribute attribute;
	public AttributedElement<?,?> element;
	
	public AttributeTableRow(Attribute a, AttributedElement<?,?> e){
		this.attribute = a;
		this.element = e;
	}
	
}
