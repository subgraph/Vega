/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.http.intercept.InterceptView;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class InterceptForwardTransaction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InterceptView view;
		try {
			view = (InterceptView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(InterceptView.ID);
		} catch (PartInitException e) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDialog.displayExceptionError(shell, e);
			return null;
		}
		view.forwardTransaction();
		return null;
	}

}
