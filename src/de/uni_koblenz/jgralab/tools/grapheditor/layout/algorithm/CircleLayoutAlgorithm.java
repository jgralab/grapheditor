package de.uni_koblenz.jgralab.tools.grapheditor.layout.algorithm;

import java.util.Vector;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.InternalNode;
import org.eclipse.zest.layouts.dataStructures.InternalRelationship;

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
public class CircleLayoutAlgorithm extends AbstractLayoutAlgorithm {

	private GraphEditor editor;

	private int totalSteps;
	private int currentStep;

	private Vertex root;

	public CircleLayoutAlgorithm() {
		super(LayoutStyles.NONE);
	}

	@Override
	protected void applyLayoutInternal(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider, double boundsX,
			double boundsY, double boundsWidth, double boundsHeight) {

		this.totalSteps = entitiesToLayout.length;
		double radius = Math.min(boundsWidth, boundsHeight) / 3.0;
		double angle = Math.PI * 2.0 / (this.totalSteps - 1);
		double midX = boundsWidth / 2.0;
		double midY = boundsHeight / 2.0;

		Vector<LayoutEntity> withoutRoot = new Vector<LayoutEntity>();

		int anglecounter = 0;
		for (this.currentStep = 0; this.currentStep < entitiesToLayout.length; this.currentStep++) {
			LayoutEntity layoutEntity = entitiesToLayout[this.currentStep]
					.getLayoutEntity();
			if (entitiesToLayout[this.currentStep].getWidthInLayout() > 250) {
				entitiesToLayout[this.currentStep].setSize(250,
						entitiesToLayout[this.currentStep].getHeightInLayout());
			}
			Vertex vertex = (Vertex) ((GraphNode) layoutEntity.getGraphData())
					.getData();
			if (this.editor.getPinMarker().isMarked(vertex)) {
				// do not layout pinned
			} else if (this.root.equals(vertex)) {
				layoutEntity.setLocationInLayout(
						midX - layoutEntity.getWidthInLayout() / 2.0, midY
								- layoutEntity.getHeightInLayout() / 2.0);
			} else {
				withoutRoot.add(layoutEntity);
				double x = midX
						+ (radius * Math.sin(angle * anglecounter) - layoutEntity
								.getWidthInLayout() / 2.0);
				double y = midY
						+ (radius * Math.cos(angle * anglecounter) - layoutEntity
								.getHeightInLayout() / 2.0);
				layoutEntity.setLocationInLayout(x, y);
				anglecounter = anglecounter + 1;
			}
			this.fireProgressEvent(this.currentStep, this.totalSteps);
		}

		// shift from the middle of the circle to the outside until there is no
		// intersection with neighbors
		for (int i = 1; i < withoutRoot.size() - 1; i = i + 2) {
			LayoutEntity layoutEntity = withoutRoot.get(i);
			LayoutEntity leftLayoutEntity = withoutRoot.get(i - 1);
			LayoutEntity rightLayoutEntity = withoutRoot.get(i + 1);
			double temprad = radius;

			while (this.intersect(leftLayoutEntity, layoutEntity)) {
				temprad += 10;
				double x = midX
						+ (temprad * Math.sin(angle * i) - layoutEntity
								.getWidthInLayout() / 2.0);
				double y = midY
						+ (temprad * Math.cos(angle * i) - layoutEntity
								.getHeightInLayout() / 2.0);
				layoutEntity.setLocationInLayout(x, y);
				if (x <= 0 || x >= boundsWidth || y <= 0 || y >= boundsHeight) {
					break;
				}
			}
			while (this.intersect(layoutEntity, rightLayoutEntity)) {
				temprad += 10;
				double x = midX
						+ (temprad * Math.sin(angle * i) - layoutEntity
								.getWidthInLayout() / 2.0);
				double y = midY
						+ (temprad * Math.cos(angle * i) - layoutEntity
								.getHeightInLayout() / 2.0);
				layoutEntity.setLocationInLayout(x, y);
				if (x <= 0 || x >= boundsWidth || y <= 0 || y >= boundsHeight) {
					break;
				}
			}
		}
		this.fireProgressEnded(this.totalSteps);
	}

	private boolean intersect(LayoutEntity one, LayoutEntity two) {

		double onex = one.getXInLayout() + one.getWidthInLayout() / 2.0;
		double oney = one.getYInLayout() + one.getHeightInLayout() / 2.0;
		double twox = two.getXInLayout() + two.getWidthInLayout() / 2.0;
		double twoy = two.getYInLayout() + two.getHeightInLayout() / 2.0;
		if (Math.abs(onex - twox) < (one.getWidthInLayout() / 2.0)
				+ (two.getWidthInLayout() / 2.0)) {
			if (Math.abs(oney - twoy) < (one.getHeightInLayout() / 2.0)
					+ (two.getHeightInLayout() / 2.0)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected int getTotalNumberOfLayoutSteps() {
		return this.totalSteps;
	}

	@Override
	protected int getCurrentLayoutStep() {
		return 0;
	}

	@Override
	public void setLayoutArea(double x, double y, double width, double height) {
	}

	@Override
	protected void preLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider, double x, double y,
			double width, double height) {

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
	protected void postLayoutAlgorithm(InternalNode[] entitiesToLayout,
			InternalRelationship[] relationshipsToConsider) {
	}

	@Override
	protected boolean isValidConfiguration(boolean asynchronous,
			boolean continuous) {
		return true;
	}

}
