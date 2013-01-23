package de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * CollectionContentProvider doing the same as ArrayContentProvider
 * 
 * @author kheckelmann
 * 
 */
public class CollectionContentProvider implements IStructuredContentProvider {

	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object parent) {
		if (parent instanceof Collection) {
			return ((Collection<?>) parent).toArray();
		}
		return new Object[0];
	}

}