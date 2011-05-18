package com.subgraph.vega.ui.scanner.commands;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.wizards.NewScanWizard;

public class StartNewScanHandler extends AbstractHandler {
	private String lastTargetValue = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IScanner scanner = Activator.getDefault().getScanner();

		NewScanWizard wizard = new NewScanWizard();
		if(lastTargetValue != null)
			wizard.setTargetField(lastTargetValue);
		wizard.setScannerModules(scanner.getAllModules());
		
		if(scanner != null) {
			IScannerConfig scannerConfig = scanner.createScannerConfig();
		
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			if(dialog.open() == IDialogConstants.OK_ID) {
				if(wizard.isDomTest()) {
					runDomTest();
					return null;
				}
				
				URI uri = wizard.getScanHostURI();
				if(uri != null) {
					lastTargetValue = wizard.getTargetField();
					scannerConfig.setBaseURI(uri);
					scannerConfig.setCookieString(wizard.getCookieString());
					scannerConfig.setBasicUsername(wizard.getBasicUsername());
					scannerConfig.setBasicPassword(wizard.getBasicPassword());
					scannerConfig.setBasicRealm(wizard.getBasicRealm());
					scannerConfig.setBasicDomain(wizard.getBasicDomain());
					scannerConfig.setExclusions(wizard.getExclusions());
					scannerConfig.setNtlmUsername(wizard.getNtlmUsername());
					scannerConfig.setNtlmPassword(wizard.getNtlmPassword());
					final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
					scannerConfig.setLogAllRequests(preferences.getBoolean("LogAllRequests"));
					scannerConfig.setDisplayDebugOutput(preferences.getBoolean("DisplayDebugOutput"));
					scanner.setScannerConfig(scannerConfig);
					scanner.startScanner(scannerConfig);
				}
			}
		}
		return null;
	}

	
	private void runDomTest() {
		IScanner scanner = Activator.getDefault().getScanner();
		if(scanner != null) {
			scanner.runDomTests();
		}
	}
}
