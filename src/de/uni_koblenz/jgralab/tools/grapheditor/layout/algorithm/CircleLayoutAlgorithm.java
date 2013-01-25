package de.uni_koblenz.jgralab.tools.grapheditor.layout.algorithm;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;

/**
 * Layout algorithm that places the selected vertex in the center and the
 * adjacent vertices around
 * 
 * @author kheckelmann
 * 
 */
public class CircleLayoutAlgorithm implements LayoutAlgorithm {

	private GraphEditor editor;

	private int totalSteps;
	private int currentStep;

	private Vertex root;

	private LayoutContext context;

	public CircleLayoutAlgorithm() {

	}

	private void applyLayoutInternal(EntityLayout[] entitiesToLayout,
			double boundsX, double boundsY, double boundsWidth,
			double boundsHeight) {

		this.totalSteps = entitiesToLayout.length;
		double radius = Math.min(boundsWidth, boundsHeight) / 3.0;
		double angle = Math.PI * 2.0 / (this.totalSteps - 1);
		double midX = boundsWidth / 2.0;
		double midY = boundsHeight / 2.0;

		Vector<EntityLayout> withoutRoot = new Vector<EntityLayout>();

		int anglecounter = 0;
		for (this.currentStep = 0; this.currentStep < entitiesToLayout.length; this.currentStep++) {
			EntityLayout layoutEntity = entitiesToLayout[this.currentStep];
			if (entitiesToLayout[this.currentStep].getSize().width > 250) {
				entitiesToLayout[this.currentStep].setSize(250,
						entitiesToLayout[this.currentStep].getSize().height);
			}

			Vertex vertex = (Vertex) ((GraphNode) layoutEntity.getItems()[0])
					.getData();
			if (this.editor.getPinMarker().isMarked(vertex)) {
				// do not layout pinned
			} else if (this.root.equals(vertex)) {
				layoutEntity.setLocation(midX - layoutEntity.getSize().width
						/ 2.0, midY - layoutEntity.getSize().height / 2.0);
			} else {
				withoutRoot.add(layoutEntity);
				double x = midX
						+ (radius * Math.sin(angle * anglecounter) - layoutEntity
								.getSize().width / 2.0);
				double y = midY
						+ (radius * Math.cos(angle * anglecounter) - layoutEntity
								.getSize().height / 2.0);
				layoutEntity.setLocation(x, y);
				anglecounter = anglecounter + 1;
			}
		}

		// shift from the middle of the circle to the outside until there is no
		// intersection with neighbors
		for (int i = 1; i < withoutRoot.size() - 1; i = i + 2) {
			EntityLayout layoutEntity = withoutRoot.get(i);
			EntityLayout leftLayoutEntity = withoutRoot.get(i - 1);
			EntityLayout rightLayoutEntity = withoutRoot.get(i + 1);
			double temprad = radius;

			while (this.intersect(leftLayoutEntity, layoutEntity)) {
				temprad += 10;
				double x = midX
						+ (temprad * Math.sin(angle * i) - layoutEntity
								.getSize().width / 2.0);
				double y = midY
						+ (temprad * Math.cos(angle * i) - layoutEntity
								.getSize().height / 2.0);
				layoutEntity.setLocation(x, y);
				if (x <= 0 || x >= boundsWidth || y <= 0 || y >= boundsHeight) {
					break;
				}
			}
			while (this.intersect(layoutEntity, rightLayoutEntity)) {
				temprad += 10;
				double x = midX
						+ (temprad * Math.sin(angle * i) - layoutEntity
								.getSize().width / 2.0);
				double y = midY
						+ (temprad * Math.cos(angle * i) - layoutEntity
								.getSize().height / 2.0);
				layoutEntity.setLocation(x, y);
				if (x <= 0 || x >= boundsWidth || y <= 0 || y >= boundsHeight) {
					break;
				}
			}
		}
	}

	private boolean intersect(EntityLayout one, EntityLayout two) {

		double onex = one.getLocation().x + one.getSize().width / 2.0;
		double oney = one.getLocation().y + one.getSize().height / 2.0;
		double twox = two.getLocation().x + two.getSize().width / 2.0;
		double twoy = two.getLocation().y + two.getSize().height / 2.0;

		if (Math.abs(onex - twox) < (one.getSize().width / 2.0)
				+ (two.getSize().width / 2.0)) {
			if (Math.abs(oney - twoy) < (one.getSize().height / 2.0)
					+ (two.getSize().height / 2.0)) {
				return true;
			}
		}
		return false;
	}

	private void preLayoutAlgorithm() {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		if (!(page.getActiveEditor() instanceof GraphEditor)) {
			return;
		}
		GraphEditor editor = (GraphEditor) page.getActiveEditor();
		this.editor = editor;

		ISelection selection = editor.getSite().getSelectionProvider()
				.getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj != null) {
				GraphElement<?, ?> element = (GraphElement<?, ?>) obj;
				if (element instanceof Vertex) {
					this.root = (Vertex) element;
				}
			}
		}
		GraphViewer viewer = (GraphViewer) editor.getZoomableViewer();
		Graph g = (Graph) viewer.getInput();
		if (this.root == null || !this.root.getGraph().equals(g)) {
			this.root = g.getFirstVertex();
		}

	}

	@Override
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	@Override
	public void applyLayout(boolean clean) {
		this.preLayoutAlgorithm();
		this.applyLayoutInternal(this.context.getEntities(),
				this.context.getBounds().x, this.context.getBounds().y,
				this.context.getBounds().width, this.context.getBounds().height);

	}
}
