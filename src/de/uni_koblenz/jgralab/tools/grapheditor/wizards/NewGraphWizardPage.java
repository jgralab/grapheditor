package de.uni_koblenz.jgralab.tools.grapheditor.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class NewGraphWizardPage extends WizardPage {

	private Text schemaPathText;
	private Text graphIDText;
	
	
	public NewGraphWizardPage(){
		super("New Graph");
	}
	
	public String getSchemaPath(){
		return schemaPathText.getText();
	}
	
	public String getGraphID(){
		return graphIDText.getText();
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		
		Label enterSchemaLabel = new Label(container, SWT.NONE);
		enterSchemaLabel.setText("Schema: ");
		schemaPathText = new Text(container, SWT.BORDER);
		schemaPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		schemaPathText.setEditable(false);
		Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				Shell shell = window.getShell();
			
				FileDialog fileDialog = new FileDialog(shell);
				fileDialog.setText("Select a Schema file");
				fileDialog.setFilterExtensions(new String [] {"*.tg", "*.xmi"});
				fileDialog.setFilterNames(new String [] {"TG-files (*.tg)", "RSA-files (*.xmi)"});
				String path = fileDialog.open();
				
				if(path != null){
					schemaPathText.setText(path);
					if(!graphIDText.getText().isEmpty()) {
						setPageComplete(true);	
						setErrorMessage(null);
						setMessage("Create a new Graph.");
					}
				}		
			}
		});
		
		Label enterGraphIDLabel = new Label(container, SWT.NONE);
		enterGraphIDLabel.setText("Graph ID: ");
		graphIDText = new Text(container, SWT.BORDER);
		graphIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		graphIDText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if(((Text)e.widget).getText().isEmpty()){
					setPageComplete(false);
					setErrorMessage("Graph ID is empty.");
				}else{
					if(!schemaPathText.getText().isEmpty()){
						setPageComplete(true);
						setErrorMessage(null);
					}else{
						setErrorMessage("Choose a Schema");
					}
				}		
			}
		});
		setControl(container);
		setPageComplete(false);	
	}

}
