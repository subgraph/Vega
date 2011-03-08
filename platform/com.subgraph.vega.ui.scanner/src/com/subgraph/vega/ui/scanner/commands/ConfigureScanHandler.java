package com.subgraph.vega.ui.scanner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import com.subgraph.vega.ui.scanner.dialogs.ScanConfigDialog;

public class ConfigureScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ScanConfigDialog dialog = new ScanConfigDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell());
		dialog.create();
		dialog.open();
		return null;
	}


}
