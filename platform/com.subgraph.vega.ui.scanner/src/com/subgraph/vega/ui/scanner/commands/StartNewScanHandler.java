package com.subgraph.vega.ui.scanner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import com.subgraph.vega.ui.scanner.wizards.NewScanWizard;

public class StartNewScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Auto-generated method stub

	NewScanWizard wizard = new NewScanWizard();
	WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
	dialog.open();
	return null;
	}


}
