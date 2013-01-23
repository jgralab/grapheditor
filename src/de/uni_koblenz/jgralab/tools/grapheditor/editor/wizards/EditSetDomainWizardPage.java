package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.pcollections.ArrayPSet;
import org.pcollections.PCollection;
import org.pcollections.PSet;

import de.uni_koblenz.jgralab.schema.SetDomain;

public class EditSetDomainWizardPage extends EditCollectionDomainWizardPage {

	public EditSetDomainWizardPage (SetDomain domain, PSet<?> value){
		super("Set Editor",domain, value);
		setTitle("Set Editor");
	}

	@Override
	protected PCollection<?> getEmptyInput() {
		return ArrayPSet.empty();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected PCollection<?> editElement(int selectionIndex,Object selection,
			PCollection value, Object newValue) {
			value = value.minus(selection);
			value = value.plus(newValue);
		return value;
	}

}
