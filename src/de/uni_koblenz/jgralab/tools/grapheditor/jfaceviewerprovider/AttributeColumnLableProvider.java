package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.tools.grapheditor.Activator;
import de.uni_koblenz.jgralab.tools.grapheditor.properties.AttributeTableRow;

/**
 * LabelProvider for the columns of the attributes table in the properties
 * sheet, specifies how the attributes are shown
 * 
 * @author kheckelmann
 * 
 */
public class AttributeColumnLableProvider extends ColumnLabelProvider {

	private static final Image CHECKED = Activator.getImageDescriptor(
			"icons/checked.gif").createImage();
	private static final Image UNCHECKED = Activator.getImageDescriptor(
			"icons/unchecked.gif").createImage();

	private int columnIndex;

	/**
	 * Constructor
	 * 
	 * @param i
	 *            column index
	 */
	public AttributeColumnLableProvider(int i) {
		this.columnIndex = i;
	}

	@Override
	public String getText(Object element) {
		AttributeTableRow row = (AttributeTableRow) element;
		// First column: attribute names
		if (this.columnIndex == 0) {
			return row.attribute.getName();
		}
		// Third column: attribute domains
		if (this.columnIndex == 2) {
			return row.attribute.getDomain().getQualifiedName();
		}
		// Second column: attribute value
		// -- Boolean values as checkbox images
		if (row.attribute.getDomain() instanceof BooleanDomain) {
			return null;
		}
		// -- Other values as Strings
		Object o = row.element.getAttribute(row.attribute.getName());
		if (o != null) {
			return this.getStringRepresentation(o);// o.toString();
		} else {
			return "<null>";
		}
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
	public Image getImage(Object element) {
		// No images as names or domains
		if (this.columnIndex == 0) {
			return null;
		}
		if (this.columnIndex == 2) {
			return null;
		}
		// Image only for BooleanDomains
		AttributeTableRow row = (AttributeTableRow) element;
		if (row.attribute.getDomain() instanceof BooleanDomain) {
			if (row.element.getAttribute(row.attribute.getName())) {
				return CHECKED;
			} else {
				return UNCHECKED;
			}
		}
		return null;
	}

}
