package com.subgraph.vega.ui.http.commands;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.http.proxy.IProxyTransaction;

public class DropTransaction extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;

			for (Iterator<?> iterator = strucSelection.iterator(); iterator.hasNext();) {
				IProxyTransaction transaction = (IProxyTransaction) iterator.next();
				transaction.doDrop();
			}			
		}
		return null;
	}
	
}
