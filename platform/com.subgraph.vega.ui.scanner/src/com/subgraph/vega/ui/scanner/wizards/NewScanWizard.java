package com.subgraph.vega.ui.scanner.wizards;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.wizard.Wizard;

public class NewScanWizard extends Wizard {

	private NewScanWizardPage page;
	private URI scanHostURI;
	private String validTargetField;
	private boolean isDomTest;
	private String targetField;
	
	@Override
	public void addPages() {
		page = new NewScanWizardPage(targetField);
		addPage(page);
	}
	
	@Override 
	public boolean canFinish() {
		final String scanHostText = page.getText();
		if(scanHostText.isEmpty())
			return false;
		
		return (createTargetURI(scanHostText) != null);

	}
	
	@Override
	public boolean performFinish() {
		String target = page.getText();
		if(target.equals("domtest")) {
			isDomTest = true;
			return true;
		}
		scanHostURI = createTargetURI(page.getText());
		if(scanHostURI != null)
			validTargetField = target;
		return (scanHostURI != null);		
	}
	
	public void setTargetField(String value) {
		targetField = value;
	}
	
	public URI getScanHostURI() {
		return scanHostURI;
	}
	
	public String getTargetField() {
		return validTargetField;
	}
	
	public boolean isDomTest() {
		return isDomTest;	
	}
	
	URI createTargetURI(String value) {
		if(!(value.startsWith("http://") || value.startsWith("https://"))) {
			value = "http://"+ value;
		}
		try {
			return new URI(value);
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
