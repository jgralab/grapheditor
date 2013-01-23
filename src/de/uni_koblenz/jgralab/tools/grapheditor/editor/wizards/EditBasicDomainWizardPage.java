package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.uni_koblenz.jgralab.tools.grapheditor.editor.domainhandling.DomainEditorUtil;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.Domain;

public class EditBasicDomainWizardPage extends WizardPage {

	private Composite container;
	private Domain domain;
	private Object value;
	private Text text;
	
	public EditBasicDomainWizardPage(BasicDomain dom, Object v){
		super(dom.getQualifiedName()+" Editor");
		setTitle(dom.getQualifiedName() + " Editor");
		this.value = v;
		this.domain = dom;
	}
	
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		text = new Text(container, SWT.BORDER);
		if(value != null){
			text.setText(value.toString());
		}
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		DomainEditorUtil.addVerifyListener((BasicDomain) domain, text);
		
		setControl(container);
		setPageComplete(true);	
	}

	
	
	/**
	 * @return the text
	 */
	public Text getText() {
		return text;
	}

}
