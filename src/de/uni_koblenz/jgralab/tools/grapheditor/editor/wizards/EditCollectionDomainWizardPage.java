package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pcollections.PCollection;

import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.CollectionContentProvider;
import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.SimpleLabelProvider;
import de.uni_koblenz.jgralab.schema.CollectionDomain;

public abstract class EditCollectionDomainWizardPage extends WizardPage {

	private Composite container;
	
	private ListViewer viewer;
	
	private CollectionDomain domain;
	private PCollection<?> value;
	
	public EditCollectionDomainWizardPage(String name, CollectionDomain dom, PCollection<?> v) {
		super(name);
		this.domain = dom;
		this.value = v;
	}
	
	/**
	 * Initializes the GUI of the Collection Editor
	 */
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
				
		this.viewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL);
		this.viewer.setLabelProvider(new SimpleLabelProvider());
		this.viewer.setContentProvider(new CollectionContentProvider());
		this.viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if(this.value != null){
			this.viewer.setInput(this.value);
		}
						
		Composite buttonContainer = new Composite(container, SWT.NONE);
		GridLayout butLay = new GridLayout();
		butLay.numColumns = 1;
		buttonContainer.setLayout(butLay);
		
		addAddButton(buttonContainer);
		addDeleteButton(buttonContainer);
		addEditButton(buttonContainer);
		
		setControl(container);
		setPageComplete(false);
	}
	
	/**
	 * @return an empty List or Set, depending on the concrete type
	 */
	protected abstract PCollection<?> getEmptyInput();
	
	/**
	 * Edits an element of an PCollection. If it is a list, the index
	 * is preserved. A set has no index.
	 * 
	 * @param selectionIndex
	 * @param selection
	 * @param original
	 * @param newValue
	 * @return the edited PCollection
	 */
	protected abstract PCollection<?> editElement(int selectionIndex, 
			Object selection, 
			PCollection<?> original, 
			Object newValue );
		
	/**
	 * Adds an edit button to the GUI
	 * 
	 * @param container
	 */
	private void addEditButton(Composite container) {
		Button editButton = new Button(container, SWT.PUSH);
		editButton.setText("edit element");
		editButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		editButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				
				PCollection<?> collectionValue = (PCollection<?>) viewer.getInput();
				int selectionIndex = viewer.getList().getSelectionIndex();
				Object selectedValue = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				
				Object editedValue = DomainEditorWizardProvider.getEditResult(domain.getBaseDomain(), selectedValue);
				collectionValue = editElement(selectionIndex,selectedValue, collectionValue, editedValue);
				viewer.setInput(collectionValue);
				
				setPageComplete(true);
			}
		});
	}
	
	/**
	 * Adds a delete button to the GUI
	 * 
	 * @param container
	 */
	private void addDeleteButton(Composite container) {
		Button deleteButton = new Button(container,SWT.PUSH);
		deleteButton.setText("delete element");
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		deleteButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				PCollection<?> input = (PCollection<?>) viewer.getInput();
				input = input.minus(((IStructuredSelection)viewer.getSelection()).getFirstElement());
				viewer.setInput(input);
				setPageComplete(true);
			}
		});
	}
		
	/**
	 * Adds an add button to the GUI 
	 * 
	 * @param container
	 */
	private void addAddButton(Composite container) {
		Button addButton = new Button(container, SWT.PUSH);
		addButton.setText("add element");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		addButton.addSelectionListener(new SelectionAdapter(){
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void widgetSelected(SelectionEvent e) {
				PCollection value = (PCollection) viewer.getInput();
								
				Object newValue = DomainEditorWizardProvider.getEditResult(domain.getBaseDomain(), null);
				if(newValue == null) return;		
				if(value == null) value = getEmptyInput();
				value = value.plus(newValue);
			
				viewer.setInput(value);
				
				setPageComplete(true);		
			}
		});
	}
	
	/**
	 * @return the viewer
	 */
	public ListViewer getViewer() {
		return viewer;
	}
}
