package com.subgraph.vega.ui.util.export;



import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ExportWizardPageOne extends WizardPage {

	
	private Composite container;
	private Group radioGroup;
	private String choice = "HTML";
	
	protected ExportWizardPageOne() {
		super("Output format");
		setTitle("Output format");
		setDescription("HTML or XML output format.");
		
	}

	@Override
	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NONE);
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		container.setLayoutData(gd);
		
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 1;
		
		/*Label l = new Label(container, SWT.NONE);
		l.setText("Select XML or HTML output.");*/
		
		
		radioGroup = new Group(container, SWT.SHADOW_IN);
		//radioGroup.setText("Select XML or HTML output.");
		gd.grabExcessHorizontalSpace = true;
		radioGroup.setLayoutData(gd);
		
		radioGroup.setLayout(new RowLayout(SWT.VERTICAL));
		Button htmlButton = new Button(radioGroup, SWT.RADIO);
		/*Button xmlButton = new Button(radioGroup, SWT.RADIO | SWT.);
	    xmlButton.setText("XML");*/
		htmlButton.setText("HTML");
		htmlButton.setSelection(true);
		/*xmlButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				choice = xmlButton.getText();
			}
		});
		*/
		
		htmlButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				choice = htmlButton.getText();
			}
		});
		
		setControl(container);
		
	}
	
	protected String getChoice() {
		return this.choice;
	}

}
