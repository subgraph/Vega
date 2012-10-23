package com.subgraph.vega.ui.scanner.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.scanner.alerts.ScanAlertView;

public class AlertScopeFilterHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if(event.getTrigger() instanceof Event) {
			final Event e = (Event) event.getTrigger();
			if(e.widget instanceof ToolItem) {
				final ToolItem item = (ToolItem) e.widget;
				if(activePart instanceof ScanAlertView) {
				((ScanAlertView) activePart).setFilterByScope(item.getSelection());
				}
			}
		}
		return null;
	}
}
