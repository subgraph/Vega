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

public class StopScannerHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ScanAlertView view;
		try {
			view = (ScanAlertView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(ScanAlertView.ID);
		} catch (PartInitException e) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDialog.displayExceptionError(shell, e);
			return null;
		}
		final IScan scan = view.getSelection();
		if (scan != null) {
			scan.stopScan();
		}
		return null;
	}

}
