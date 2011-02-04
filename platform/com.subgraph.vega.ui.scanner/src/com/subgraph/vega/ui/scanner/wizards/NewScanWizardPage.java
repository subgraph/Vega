package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class NewScanWizardPage extends WizardPage {

	private Composite container;
	private Text text;
	private String targetValue;
	
	public NewScanWizardPage(String targetValue) {
		super("Create a New Scan");
		setTitle("Create a New Scan");
		setDescription("Create a New Scan");
		this.targetValue = targetValue;
	}
	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		Label label = new Label(container, SWT.NULL);
		label.setText("Input host to scan:");
		
		text = new Text(container, SWT.BORDER | SWT.SINGLE);
		if(targetValue != null)
			text.setText(targetValue);
		else
			text.setText("");
		text.addKeyListener(new KeyListener() {
			@Override 
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (!text.getText().isEmpty()) {
					setPageComplete(true);
				}
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);
		setControl(container);
		setPageComplete(false);
		
	}
		
	public String getText() {
		return text.getText();
	}			
}