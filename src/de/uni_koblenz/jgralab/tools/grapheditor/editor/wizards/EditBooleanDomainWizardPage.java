package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.uni_koblenz.jgralab.schema.BooleanDomain;

public class EditBooleanDomainWizardPage extends WizardPage {

	
	private Composite container;
	private Boolean value;
	private Button checkbox;
	
	public EditBooleanDomainWizardPage(BooleanDomain dom, Boolean val){
		super("Boolean Editor");
		setTitle("Boolean Editor");
		this.value = val;
	}
	
	@Override
	public void createControl(Composite parent) {
		this.container = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		this.checkbox = new Button(this.container, SWT.CHECK);
		if(this.value ==null) this.value = false;
		if(this.value){
			checkbox.setSelection(true);
		}else{
			checkbox.setSelection(false);
		}
		checkbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(true);			
			}
		});
		
		setControl(container);
		setPageComplete(true);	

	}
	
	public boolean getSelected(){
		return checkbox.getSelection();
	}

}
