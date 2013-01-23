package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.pcollections.PCollection;
import org.pcollections.PSet;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.schema.CollectionDomain;
import de.uni_koblenz.jgralab.schema.ListDomain;
import de.uni_koblenz.jgralab.schema.SetDomain;

public class EditCollectionDomainWizard extends Wizard {

	private EditCollectionDomainWizardPage page;
	
	private CollectionDomain domain;
	private PCollection<?> value;
	
	public EditCollectionDomainWizard(CollectionDomain dom, PCollection<?> val){
		super();
		this.domain = dom;
		this.value = val;
	}
	
	@Override
	public void addPages(){
		if(domain instanceof ListDomain)
			page = new EditListDomainWizardPage((ListDomain)domain, (PVector<?>)value);
		else 
			page = new EditSetDomainWizardPage((SetDomain)domain, (PSet<?>)value);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		PVector<?> newValue = (PVector<?>) page.getViewer().getInput();
		this.value = newValue;
		return true;
	}

	/**
	 * @return the value
	 */
	public PCollection<?> getValue() {
		return value;
	}

}
