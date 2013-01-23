package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * Label provider that just provides the String representation as label
 * 
 * @author kheckelmann
 * 
 */
public class SimpleLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		return element.toString();
	}

}