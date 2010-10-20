package com.subgraph.vega.ui.scanner.dashboard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.scanner.info.ScanInfoView;

public class ShowDashboardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if(activePart instanceof ScanInfoView) {
			((ScanInfoView) activePart).showDashboard();
		}
		return null;
	}
}
