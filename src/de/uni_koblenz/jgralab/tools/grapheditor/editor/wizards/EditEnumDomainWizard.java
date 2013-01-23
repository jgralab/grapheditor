package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.Wizard;

import de.uni_koblenz.jgralab.tools.grapheditor.editor.domainhandling.DomainEditorUtil;
import de.uni_koblenz.jgralab.schema.EnumDomain;

public class EditEnumDomainWizard extends Wizard {

	private EditEnumDomainWizardPage page;
	
	private EnumDomain domain;
	private Object value;
	
	public EditEnumDomainWizard(EnumDomain dom, Object val){
		super();
		this.domain = dom;
		this.value = val;
	}
	
	@Override
	public void addPages(){
		this.page = new EditEnumDomainWizardPage(domain, value);
		addPage(this.page);
	}
	
	@Override
	public boolean performFinish() {
		int i = page.getCombo().getSelectionIndex();
		if(i == page.getCombo().getItemCount()-1){
			this.value = null;
		}else{
			this.value = page.getCombo().getItem(i);
			this.value = DomainEditorUtil.createEnum(this.domain, this.value);
		}
		return true;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

}
