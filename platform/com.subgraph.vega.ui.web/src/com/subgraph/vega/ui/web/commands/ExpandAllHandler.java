package com.subgraph.vega.ui.web.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.web.views.WebsiteView;

public class ExpandAllHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if(activePart instanceof WebsiteView) {
			((WebsiteView)activePart).expandAll();
		}
		return null;
	}

}
