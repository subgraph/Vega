package com.subgraph.vega.ui.scanner.wizards;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.wizard.Wizard;

public class NewScanWizard extends Wizard {

	private NewScanWizardPage page;
	private URI scanHostURI;
	private boolean isDomTest;
	
	@Override
	public void addPages() {
		page = new NewScanWizardPage();
		addPage(page);
	}
	
	@Override 
	public boolean canFinish() {
		final String scanHostText = page.getText();
		if(scanHostText.isEmpty())
			return false;
		try {
			new URI(scanHostText);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
	
	@Override
	public boolean performFinish() {
		String target = page.getText();
		if(target.equals("domtest")) {
			isDomTest = true;
			return true;
		}
		
		try {
			scanHostURI = new URI(page.getText());
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
		
	}
	
	public URI getScanHostURI() {
		return scanHostURI;
	}
	
	public boolean isDomTest() {
		return isDomTest;
		
	}

}
