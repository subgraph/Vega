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
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.util.dialogs.ErrorDialog;

public class ProxyPassthrough extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		if(proxyService == null) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDialog.displayError(shell, "Proxy service is null");
			return null;
		}
		
		ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil.getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
		ProxyStateSourceProvider proxyState = (ProxyStateSourceProvider) sourceProviderService.getSourceProvider(ProxyStateSourceProvider.PASSTHROUGH_STATE);
		if(proxyState == null) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDialog.displayError(shell, "Proxy state provider is null");
			return null;
		}

		final boolean toggle = !proxyService.isPassthrough();
		proxyService.setPassthrough(toggle);
		proxyState.setProxyPassthrough(toggle);
		
		return null;
	}

}
