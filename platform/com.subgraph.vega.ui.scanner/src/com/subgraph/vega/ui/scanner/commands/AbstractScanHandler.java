package com.subgraph.vega.ui.scanner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.ui.scanner.alerts.ScanAlertView;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public abstract class AbstractScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ScanAlertView scanAlertView = getScanAlertView(event);
		if(scanAlertView == null) {
			return null;
		}
		final IScan scan = scanAlertView.getSelection();
		if(scan != null) {
			runCommand(event, scan);
		}
		return null;
	}
	
	private ScanAlertView getScanAlertView(ExecutionEvent event) {
		try {
			return (ScanAlertView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(ScanAlertView.ID);
		} catch (PartInitException e) {
			final Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDialog.displayExceptionError(shell, e);
			return null;
		}
	}
	
	abstract protected void runCommand(ExecutionEvent event, IScan selectedScan);
		


}
