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

import java.net.URISyntaxException;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.http.requesteditviewer.RequestEditView;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class ReplayRequest extends AbstractHandler {
	private static final int MAX_OPEN_EDITORS = 3; /** Maximum number of editors to allow to open at once before prompting the user */
	
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;

			if (strucSelection.size() > MAX_OPEN_EDITORS) {
				Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
				MessageBox messageDialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
				messageDialog.setText("Warning");
				messageDialog.setMessage(strucSelection.size() + " replay editors will be opened. Proceed?");
				if (messageDialog.open() == SWT.CANCEL) {
					return null;
				}
			}

			int viewMode = IWorkbenchPage.VIEW_ACTIVATE;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				RequestEditView view;

				try {
					view = (RequestEditView) HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(RequestEditView.VIEW_ID, element.toString(), viewMode);
				} catch (PartInitException e) {
					Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
					ErrorDialog.displayExceptionError(shell, e);
					return null;
				}

				try {
					view.setRequest((IRequestLogRecord) element);
				} catch (URISyntaxException e) {
					Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
					ErrorDialog.displayExceptionError(shell, e);
					return null;
				}
				viewMode = IWorkbenchPage.VIEW_VISIBLE;
			}
		}
		return null;
	}

}
