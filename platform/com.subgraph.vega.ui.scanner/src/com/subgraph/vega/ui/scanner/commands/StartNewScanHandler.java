package com.subgraph.vega.ui.scanner.commands;

import java.net.URI;
import java.util.List;

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
	private String lastTargetValue = null;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IScanner scanner = Activator.getDefault().getScanner();

		NewScanWizard wizard = new NewScanWizard();
		if(lastTargetValue != null)
			wizard.setTargetField(lastTargetValue);
		
		if(scanner != null) {
			IScannerConfig scannerConfig = scanner.createScannerConfig();
		
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			if(dialog.open() == IDialogConstants.OK_ID) {
				if(wizard.isDomTest()) {
					runDomTest();
					return null;
				}
				
				URI uri = wizard.getScanHostURI();
				String cookieString = wizard.getCookieString();
				String basicUsername = wizard.getBasicUsername();
				String basicPassword = wizard.getBasicPassword();
				String basicRealm = wizard.getBasicRealm();
				String basicDomain = wizard.getBasicDomain();
				String ntlmUsername = wizard.getNtlmUsername();
				String ntlmPassword = wizard.getNtlmPassword();
				List<String> exclusions = wizard.getExclusions();
				
				
				if(uri != null) {
					lastTargetValue = wizard.getTargetField();
				
					scannerConfig.setBaseURI(uri);
					scannerConfig.setCookieString(cookieString);
					scannerConfig.setBasicUsername(basicUsername);
					scannerConfig.setBasicPassword(basicPassword);
					scannerConfig.setBasicRealm(basicRealm);
					scannerConfig.setBasicDomain(basicDomain);
					scannerConfig.setExclusions(exclusions);
					scannerConfig.setNtlmUsername(ntlmUsername);
					scannerConfig.setNtlmPassword(ntlmPassword);
					if(Activator.getDefault().getPreferenceStore().getBoolean("LogAllRequests"))
						scannerConfig.setLogAllRequests(true);
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
