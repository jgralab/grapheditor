package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.Wizard;

import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.StringDomain;

public class EditBasicDomainWizard extends Wizard {
	
	private EditBasicDomainWizardPage page;
	
	private BasicDomain domain;
	private Object value;
	
	public EditBasicDomainWizard(BasicDomain dom, Object val){
		super();
		this.domain = dom;
		this.value = val;
	}
	
	@Override
	public void addPages(){
		page = new EditBasicDomainWizardPage(this.domain, this.value);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		String text = page.getText().getText();
		if(text.equals("")){
			text = domain.getInitialValue();
		}			
		if(domain instanceof IntegerDomain){
			this.value = Integer.parseInt(text);
		}else if(domain instanceof LongDomain){
			this.value = Long.parseLong(text);
		}else if(domain instanceof DoubleDomain){
			this.value = Double.parseDouble(text);
		}else if(domain instanceof StringDomain){
			this.value = text;
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
