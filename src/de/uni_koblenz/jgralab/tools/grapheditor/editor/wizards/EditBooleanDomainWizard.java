package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.Wizard;

import de.uni_koblenz.jgralab.schema.BooleanDomain;

public class EditBooleanDomainWizard extends Wizard {

	private EditBooleanDomainWizardPage page;
	
	private BooleanDomain domain;
	private Boolean value;

	public EditBooleanDomainWizard(BooleanDomain dom, Boolean val){
		super();
		this.domain = dom;
		this.value = val;		
	}
	
	@Override
	public void addPages(){
		page = new EditBooleanDomainWizardPage(this.domain, this.value);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		this.value = page.getSelected();
		return true;
	}

	public Boolean getValue() {
		return value;
	}
}
