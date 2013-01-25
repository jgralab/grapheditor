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

	public EditCollectionDomainWizard(CollectionDomain dom, PCollection<?> val) {
		super();
		this.domain = dom;
		this.value = val;
	}

	@Override
	public void addPages() {
		if (this.domain instanceof ListDomain) {
			this.page = new EditListDomainWizardPage((ListDomain) this.domain,
					(PVector<?>) this.value);
		} else {
			this.page = new EditSetDomainWizardPage((SetDomain) this.domain,
					(PSet<?>) this.value);
		}
		this.addPage(this.page);
	}

	@Override
	public boolean performFinish() {
		PCollection<?> newValue = (PCollection<?>) this.page.getViewer()
				.getInput();
		this.value = newValue;
		return true;
	}

	/**
	 * @return the value
	 */
	public PCollection<?> getValue() {
		return this.value;
	}

}
