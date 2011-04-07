package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.http.requestfilters.RequestFilterDialog;

public class OpenRequestViewFilter extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		RequestFilterDialog dialog = new RequestFilterDialog(window.getShell());
		dialog.open();
		return null;
	}
}
