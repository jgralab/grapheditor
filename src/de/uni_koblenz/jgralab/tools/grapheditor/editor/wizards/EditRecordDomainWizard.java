package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.Wizard;

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.schema.RecordDomain;

public class EditRecordDomainWizard extends Wizard {

	private EditRecordDomainWizardPage page;
	
	private RecordDomain domain;
	private Record value;
	
	public EditRecordDomainWizard(RecordDomain dom, Record val){
		super();
		this.domain = dom;
		this.value = val;
	}
	
	public void addPages(){
		this.page = new EditRecordDomainWizardPage(domain, value);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		this.value = page.getValue();
		return true;
	}

	/**
	 * @return the value
	 */
	public Record getValue() {
		return value;
	}

}
