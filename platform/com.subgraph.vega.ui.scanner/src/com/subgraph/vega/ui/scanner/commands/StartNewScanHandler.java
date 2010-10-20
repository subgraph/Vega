package com.subgraph.vega.ui.scanner.commands;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.wizards.NewScanWizard;

public class StartNewScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NewScanWizard wizard = new NewScanWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		if(dialog.open() == IDialogConstants.OK_ID) {
			if(wizard.isDomTest()) {
				runDomTest();
				return null;
			}
				
			URI uri = wizard.getScanHostURI();
			if(uri != null) 
				launchScanner(uri);
		}
		return null;
	}

	private void launchScanner(URI uri) {
		IScanner scanner = Activator.getDefault().getScanner();
		if(scanner != null) {
			IScannerConfig scannerConfig = scanner.createScannerConfig();
			scannerConfig.setBaseURI(uri);
			scanner.startScanner(scannerConfig);
		}
	}
	
	private void runDomTest() {
		IScanner scanner = Activator.getDefault().getScanner();
		if(scanner != null) {
			scanner.runDomTests();
		}
	}
}
