package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.pcollections.ArrayPVector;
import org.pcollections.PCollection;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.schema.ListDomain;

public class EditListDomainWizardPage extends EditCollectionDomainWizardPage {
	
	public EditListDomainWizardPage (ListDomain domain, PVector<?> value){
		super("List Editor",domain, value);
		setTitle("List Editor");
	}

	@Override
	protected PCollection<?> getEmptyInput() {
		return ArrayPVector.empty();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected PCollection<?> editElement(int selectionIndex, Object selection,
			PCollection<?> value, Object newValue) {
		value = ((PVector)value).with(selectionIndex, newValue);
		return value;
	}
}
