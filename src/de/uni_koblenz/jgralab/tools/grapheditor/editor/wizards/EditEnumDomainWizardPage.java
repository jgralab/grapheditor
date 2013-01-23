package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.SimpleLabelProvider;
import de.uni_koblenz.jgralab.schema.EnumDomain;

public class EditEnumDomainWizardPage extends WizardPage {

	private Composite container;
	
	private ComboViewer comboViewer;
	
	private EnumDomain domain;
	private Object value;

	public EditEnumDomainWizardPage (EnumDomain domain, Object value){
		super("Enumeration Editor");
		setTitle("Enumeration Editor");
		this.domain = domain;
		this.value = value;
	}
	
	/**
	 * Initializes the GUI of the Enumeration Editor
	 */
	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);	
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		
		this.comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		this.comboViewer.setContentProvider(new ArrayContentProvider());
		this.comboViewer.setLabelProvider(new SimpleLabelProvider());
		PVector<String> content = domain.getConsts();
		this.comboViewer.setInput(content.toArray());
		this.comboViewer.add("null");
		if(value == null){
			this.comboViewer.getCombo().select(this.comboViewer.getCombo().getItemCount()-1);
		}else{
			this.comboViewer.getCombo().select(content.indexOf(value.toString()));
		}	
		this.comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(true);				
			}
		});

		setControl(container);
		setPageComplete(false);	
	}

	/**
	 * @return the combo
	 */
	public Combo getCombo() {
		return this.comboViewer.getCombo();
	}
}
