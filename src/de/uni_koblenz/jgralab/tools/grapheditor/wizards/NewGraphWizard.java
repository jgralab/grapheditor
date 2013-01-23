package de.uni_koblenz.jgralab.tools.grapheditor.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditor;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.GraphEditorInput;

public class NewGraphWizard extends Wizard implements INewWizard{

	private NewGraphWizardPage page;
	
	@Override
	public void addPages(){
		this.page = new NewGraphWizardPage();
		this.page.setTitle("Graph");
		this.page.setDescription("Create a new Graph");
		addPage(this.page);
	}
	
	@Override
	public boolean performFinish() {
		String path = page.getSchemaPath();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = (IWorkbenchPage) window.getActivePage();
		if(path != null){
			GraphEditorInput input = new GraphEditorInput(path,true, this.page.getGraphID());
			try {
				workbenchPage.openEditor(input, GraphEditor.ID);
				return true;
			} catch (PartInitException e) {
				e.printStackTrace();
			}	
		}		
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		System.err.println("workbench: " +workbench);
		System.err.println("selection: " + selection);
	}

}
