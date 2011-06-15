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
package com.subgraph.vega.ui.web.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;
import com.subgraph.vega.ui.scanner.ScanExecutor;

public class ScanFromHandler extends AbstractHandler {

	private final ScanExecutor scanExecutor = new ScanExecutor();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		final Shell shell = HandlerUtil.getActiveShell(event);
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if(ss.size() == 1 && (ss.getFirstElement() instanceof IWebEntity))
				scanFrom(shell, (IWebEntity) ss.getFirstElement());
		}
		return null;
	}
	
	private void scanFrom(Shell shell, IWebEntity item) {
		if(item instanceof IWebHost) {
			scanExecutor.runScan(shell, ((IWebHost)item).getUri().toString());
		} else if (item instanceof IWebPath) {
			scanExecutor.runScan(shell, ((IWebPath) item).getUri().toString());
		} else if (item instanceof IWebResponse) {
			final IWebPath path = ((IWebResponse) item).getPathEntity();
			if(path != null) {
				scanExecutor.runScan(shell, path.getUri().toString());
			}
		}
	}
}
