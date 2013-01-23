package de.uni_koblenz.jgralab.tools.grapheditor.editor.wizards;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.pcollections.ArrayPMap;
import org.pcollections.ArrayPSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import de.uni_koblenz.jgralab.tools.grapheditor.jfaceviewerprovider.CollectionContentProvider;
import de.uni_koblenz.jgralab.schema.MapDomain;

public class EditMapDomainWizardPage extends WizardPage {

	private Composite container;
	
	private TableViewer viewer;
	
	private MapDomain domain;
	private PMap<?,?> value;
	
	public EditMapDomainWizardPage(MapDomain dom, PMap<?,?> val){
		super("Map Editor");
		setTitle("Map Editor");
		this.domain = dom;
		this.value = val;
	}
	
	/**
	 * Initializes the GUI of the Map Editor
	 */
	@Override
	public void createControl(Composite parent) {
		// Layout and container
		this.container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		
		// Initialize the viewer
		this.viewer = new TableViewer(container,  
				SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		
		final Table table = this.viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		this.viewer.setContentProvider(new CollectionContentProvider());
		if(this.value == null){
			this.value = ArrayPMap.empty(); 
		}
		Object input = value.entrySet();
		if(!((PSet<?>)input).isEmpty())
			this.viewer.setInput(input);
		
		viewer.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// Add buttons
		Composite buttonContainer = new Composite(container, SWT.NONE);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 1;
		buttonContainer.setLayout(buttonLayout);
		createAddButton(buttonContainer);
		createDeleteButton(buttonContainer);
		createEditKeyButton(buttonContainer);
		createEditValueButton(buttonContainer);
		
		setControl(container);
		setPageComplete(false);
	}

	/**
	 * Adds an add button to the GUI
	 * 
	 * @param container
	 */
	private void createAddButton(Composite container){
		Button addButton = new Button(container, SWT.PUSH);
		addButton.setText("add entry");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		addButton.addSelectionListener(new SelectionAdapter(){
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				PSet<Entry<?,?>> entrySet = (PSet<Entry<?, ?>>) viewer.getInput();
				if(entrySet == null){
					entrySet = ArrayPSet.empty();
				}
				
				Object key = DomainEditorWizardProvider.getEditResult(domain.getKeyDomain(), null);
				Object value = DomainEditorWizardProvider.getEditResult(domain.getValueDomain(), null);	
				if(key == null || value == null) return;
				
				//assure the map property, it is not allowed to add an entry with the same key
				for(Entry<?,?> entry : entrySet){
					if(entry.getKey().equals(key)){
						entrySet = entrySet.minus(entry);
					}
				}
				
				ArrayPMap<?,?> singleElementMap = ArrayPMap.empty().plus(key,value);
				entrySet = entrySet.plusAll(singleElementMap.entrySet());				
				viewer.setInput(entrySet);

				setPageComplete(true);
			}
		});
	}
	
	/**
	 * Adds a delete button to the GUI
	 * 
	 * @param container
	 */
	private void createDeleteButton(Composite container) {
		Button deleteButton = new Button(container, SWT.PUSH);
		deleteButton.setText("delete entry");
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		deleteButton.addSelectionListener(new SelectionAdapter(){
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				Entry<?,?> entry = (Entry<?, ?>) ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if(entry == null){
					return;
				}
				PSet<Entry<?,?>> entrySet = (PSet<Entry<?, ?>>) viewer.getInput();
				entrySet = entrySet.minus(entry);
				viewer.setInput(entrySet);

				setPageComplete(true);
			}
		});
	}

	/**
	 * Adds an edit button for the key component to the GUI
	 * 
	 * @param container
	 */
	private void createEditKeyButton(Composite container) {
		Button editKeyButton = new Button(container, SWT.PUSH);
		editKeyButton.setText("edit key");
		editKeyButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		editKeyButton.addSelectionListener(new SelectionAdapter(){
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				Entry<?,?> entry = (Entry<?, ?>) ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if(entry == null){
					return;
				}
				PSet<Entry<?,?>> entrySet = (PSet<Entry<?, ?>>) viewer.getInput();

				Object editedKey = DomainEditorWizardProvider.getEditResult(domain.getKeyDomain(), entry.getKey());
				
				entrySet = entrySet.minus(entry);
				ArrayPMap<?,?> singleElementMap = ArrayPMap.empty().plus(editedKey, entry.getValue());
				entrySet = entrySet.plusAll(singleElementMap.entrySet());			
				viewer.setInput(entrySet);

				setPageComplete(true);
			}
		});
	}
	
	/**
	 * Adds an edit button for the value component to the GUI
	 * 
	 * @param container
	 */
	private void createEditValueButton(Composite container) {
		Button editValueButton = new Button(container, SWT.PUSH);
		editValueButton.setText("edit value");
		editValueButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		editValueButton.addSelectionListener(new SelectionAdapter(){
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				Entry<?,?> entry = (Entry<?, ?>) ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if(entry == null){
					return;
				}
				PSet<Entry<?,?>> entrySet = (PSet<Entry<?, ?>>) viewer.getInput();

				Object editedValue = DomainEditorWizardProvider.getEditResult(domain.getValueDomain(), entry.getValue());
			
				entrySet = entrySet.minus(entry);
				ArrayPMap<?,?> singleElementMap = ArrayPMap.empty().plus(entry.getKey(), editedValue);
				entrySet = entrySet.plusAll(singleElementMap.entrySet());
				viewer.setInput(entrySet);

				setPageComplete(true);
			}
		});
	}

	

	/**
	 * Create the columns for the Table displaying the Map
	 */
	private void createColumns() {
		// Column "Keys"
		final TableViewerColumn keyCol = createColumn("Keys");
		keyCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof Entry){
					Entry<?,?> e = (Entry<?,?>) element;
					return e.getKey().toString();
				}		
				throw new RuntimeException("Unexpected Type: "
						+element.getClass()
						+". java.util.Entry expected.");			
			}
		});
	
		// Column "Values"
		final TableViewerColumn valCol = createColumn("Values");	
		valCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof Entry){
					Entry<?,?> e = (Entry<?,?>) element;
					return e.getValue().toString();
				}
				
				throw new RuntimeException("Unexpected Type: "
						+element.getClass()
						+". java.util.Entry expected.");			
			}
		});
	}

	private TableViewerColumn createColumn(String title) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		final TableColumn col = viewerColumn.getColumn();
		col.setText(title);
		col.setWidth(200);
		col.setResizable(false);
		col.setMoveable(false);
		return viewerColumn;
	}

	/**
	 * @return the viewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}
	
	

}
