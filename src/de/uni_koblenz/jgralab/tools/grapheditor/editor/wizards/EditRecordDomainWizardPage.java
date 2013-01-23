package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Record;
import de.uni_koblenz.jgralab.tools.grapheditor.editor.domainhandling.DomainEditorUtil;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.SimpleLabelProvider;
import de.uni_koblenz.jgralab.schema.BasicDomain;
import de.uni_koblenz.jgralab.schema.BooleanDomain;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.DoubleDomain;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.IntegerDomain;
import de.uni_koblenz.jgralab.schema.LongDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain;
import de.uni_koblenz.jgralab.schema.RecordDomain.RecordComponent;

public class EditRecordDomainWizardPage extends WizardPage {

	private Composite container;
	
	private RecordDomain domain;
	private Record value;
	
	private HashMap<RecordComponent, Object> component2value;
	
	public EditRecordDomainWizardPage(RecordDomain dom, Record val){
		super("Record Editor");
		setTitle("Record Editor");
		this.domain = dom;
		this.value = val;
	}
	
	/**
	 * Initialization of the GUI of the Record Editor
	 */
	@Override
	public void createControl(Composite parent) {
		// Container and layout
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		
		if(this.value == null){
			this.value = DomainEditorUtil.createRecord(domain,getEmptyRecordValueMap());
		}
		this.component2value = new HashMap<RecordDomain.RecordComponent, Object>();
		for(RecordComponent component : domain.getComponents()){
			Label componentNameLabel = new Label(container, SWT.NONE);
			componentNameLabel.setText(component.getName());
			
			if(component.getDomain() instanceof BooleanDomain){
				createBooleanDomainComponentFields(component);
			}else if(component.getDomain() instanceof BasicDomain){
				createBasicDomainComponentFields(component);
			}else if(component.getDomain() instanceof EnumDomain){
				createEnumDomainComponentFields(component);	
			}else{
				createComponentFields(component);
			}
		}//end for
		
		setControl(container);
		setPageComplete(false);
		
		
	}
	
	
	
	/**
	 * Create simple text fields to edit Integer, Long, Double and String
	 * 
	 * @param component
	 */
	private void createBasicDomainComponentFields(RecordComponent component) {
		Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
				true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setPageComplete(true);
			}
		});
		Object attributeValue = this.value.getComponent(component.getName());
		if (attributeValue != null) {
			text.setText(attributeValue.toString());
		} else {
			text.setText("");
		}
		DomainEditorUtil.addVerifyListener((BasicDomain) component.getDomain(), text);

		// No edit button for Basic Domains
		Label empty = new Label(container, SWT.NONE);
		empty.setText(": "+component.getDomain().getQualifiedName());
		
		this.component2value.put(component, text);
	}

	
	/**
	 * Create a checkbox for Boolean attributes
	 * 
	 */
	private void createBooleanDomainComponentFields(RecordComponent component) {
		Button checkbox = new Button(container, SWT.CHECK);
		Boolean value = (Boolean)this.value.getComponent(component.getName());
		if(value){
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
		// No edit button for BooleanDomains
		Label empty = new Label(container, SWT.NONE);
		empty.setText("");
		
		this.component2value.put(component, checkbox);
	}

	
	/**
	 * Create a DropDown box to select EnumDomain constants
	 * 
	 * @param component
	 */
	private void createEnumDomainComponentFields(RecordComponent component) {
		EnumDomain enumdom = (EnumDomain) component.getDomain();
		ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new SimpleLabelProvider());
		PVector<String> content = enumdom.getConsts();
		comboViewer.setInput(content.toArray());
		comboViewer.add("null");
		Object value = this.value.getComponent(component.getName());
		if(value == null){
			comboViewer.getCombo().select(comboViewer.getCombo().getItemCount()-1);
		}else{
			comboViewer.getCombo().select(content.indexOf(value.toString()));
		}	
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(true);				
			}
		});
		
		// No edit button for EnumDomains
		Label empty = new Label(container, SWT.NONE);
		empty.setText("");
		
		this.component2value.put(component, comboViewer);
	}
	
	/**
	 * Create text field and edit button for complex domains
	 * @param component
	 */
	private void createComponentFields(RecordComponent component) {
		Text componentValueLabel = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		componentValueLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		Object componentValue = this.value.getComponent(component.getName());
		if(componentValue == null)
			componentValueLabel.setText("<null>");
		else
			componentValueLabel.setText(componentValue.toString());

		Button editButton = new Button(container, SWT.PUSH);
		editButton.setText("edit");
		editButton.setData(new Object [] {component, componentValueLabel});
		editButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				Object [] data = (Object [])((Button)e.getSource()).getData();
				RecordComponent component = (RecordComponent) data[0];
				Text componentValueLabel = (Text) data[1];
				
				Object editedValue = DomainEditorWizardProvider.getEditResult(
						component.getDomain(), 
						value.getComponent(component.getName()));
				
				// update the value
				value = DomainEditorUtil.createRecord(domain,copyRecordWithEditedComponent(
						component, editedValue));
				
				// update the text field
				componentValueLabel.setText(editedValue.toString());
				
				setPageComplete(true);
				
			}

			private Map<String, Object> copyRecordWithEditedComponent(
					RecordComponent component, Object editedValue) {
				HashMap<String, Object> compVal = new HashMap<String, Object>();
				for(RecordComponent comp : domain.getComponents()){
					if(comp.equals(component)){
						compVal.put(comp.getName(), editedValue);
					}else{
						compVal.put(comp.getName(), value.getComponent(comp.getName()));
					}
				}
				return compVal;
			}
		});
		this.component2value.put(component, componentValue);
	}

	/**
	 * Creates the components of a Record matching the RecordDomain
	 * with empty values for all 
	 * 
	 * @return empty values for all components as Map
	 */
	private Map<String, Object> getEmptyRecordValueMap() {
		HashMap<String, Object> compVal = new HashMap<String, Object>();
		for(RecordComponent comp : domain.getComponents()){
			if(comp.getDomain() instanceof IntegerDomain)
				compVal.put(comp.getName(), 0);
			else if(comp.getDomain() instanceof BooleanDomain)
				compVal.put(comp.getName(), false);
			else if(comp.getDomain() instanceof LongDomain)
				compVal.put(comp.getName(), 0);
			else if(comp.getDomain() instanceof DoubleDomain)
				compVal.put(comp.getName(), 0.0d);
			else
				compVal.put(comp.getName(), null);
		}
		return compVal;
	}


	/**
	 * @return the value
	 */
	public Record getValue() {
		HashMap<String, Object> compVal = new HashMap<String, Object>();
		for(RecordComponent comp : domain.getComponents()){
			Domain d = comp.getDomain();
			if(d instanceof BooleanDomain){
				compVal.put(comp.getName(),
						((Button)this.component2value.get(comp)).getSelection());
			}else if(d instanceof BasicDomain){
				Text t = (Text)this.component2value.get(comp);
				compVal.put(comp.getName(),DomainEditorUtil.parseDomainValue(t.getText(), (BasicDomain) d));
			}else if(d instanceof EnumDomain){
				ComboViewer c = (ComboViewer) this.component2value.get(comp);
				Combo combo = c.getCombo();
				int i = combo.getSelectionIndex();
				if(i == combo.getItemCount()-1){
					compVal.put(comp.getName(), null);
				}else{
					Object value = combo.getItem(i);
					value = DomainEditorUtil.createEnum((EnumDomain) d,value);
					compVal.put(comp.getName(), value);					
				}
			}
		}
		this.value = DomainEditorUtil.createRecord(domain,compVal);
		
		return value;
	}
	
	
}
