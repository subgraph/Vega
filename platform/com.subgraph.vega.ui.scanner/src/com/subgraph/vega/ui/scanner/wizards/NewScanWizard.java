package com.subgraph.vega.ui.scanner.wizards;

import org.eclipse.jface.wizard.Wizard;

public class NewScanWizard extends Wizard {

	private NewScanWizardPage page;
	
	@Override
	public void addPages() {
		page = new NewScanWizardPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		System.out.println(page.getText());
		return true;
	}

}
