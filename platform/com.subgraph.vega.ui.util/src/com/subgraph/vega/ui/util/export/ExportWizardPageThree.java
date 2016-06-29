package com.subgraph.vega.ui.util.export;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExportWizardPageThree extends WizardPage {

	protected FileDialog dialog;
	protected Text textField;
	protected Composite container;
	private String saveFilename = null;
	private String filename = null;
	
	final static int VISIBLE_PATH_LENGTH = 255;
	
	protected ExportWizardPageThree() {
		super("Output destination");
		

		
		setTitle("Destination for report output.");

	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		
		Label label = new Label(container, SWT.NONE);
		label.setText("Choose file:");
		textField = new Text(container, SWT.READ_ONLY | SWT.FILL);
		textField.setSize(VISIBLE_PATH_LENGTH, textField.getSize().y);
		Button button = new Button(container, SWT.NONE);
		button.setText("Open");
		
		GridData buttonGridData = new GridData();
		buttonGridData.horizontalSpan = 2;
		buttonGridData.horizontalAlignment = SWT.END;
		button.setLayoutData(buttonGridData);
		
		GridData textFieldGridData = new GridData();
		textFieldGridData.widthHint = 300;
		textField.setLayoutData(textFieldGridData);
		
		button.addSelectionListener (new SelectionListener() { 

			@Override
			public void widgetSelected(SelectionEvent e) {
				doFileDialog(parent.getShell());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}}); 
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		filename = "Vega-report-"+dateFormat.format(new Date());
		
		IWizardPage pageOne = getPreviousPage().getPreviousPage();
		String ext = ((ExportWizardPageOne) pageOne).getChoice();
		
		if (ext.equals("HTML")) {
			filename += ".html";
		} else if (ext.equals("XML")){
			filename += ".xml";
		}
		
		setPageComplete(false);
		setControl(container);
	}

	private void doFileDialog(Shell shell) {
		dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Choose an output file");

	    dialog.setFileName(filename);
	    saveFilename = dialog.open();
	    if (saveFilename != null) {    
	    	textField.setText(saveFilename);
	    	setPageComplete(true);
	    }
	}
	
	String getSaveFilename() { 
		return saveFilename;
	}
}
