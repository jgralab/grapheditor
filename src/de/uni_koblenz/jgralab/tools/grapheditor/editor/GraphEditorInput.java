package de.uni_koblenz.jgralab.tools.grapheditor.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class GraphEditorInput implements IEditorInput {

	private final String path;
	private final String graphID;

	public GraphEditorInput(String path, boolean newG, String gID) {
		this.path = path;
		this.graphID = gID;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return this.path;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Represents a Graph";
	}

	public String getPath() {
		return this.path;
	}

	public String getGraphID() {
		return this.graphID;
	}
}
