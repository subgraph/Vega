package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;

public class ProxyScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
				
		boolean oldValue = HandlerUtil.toggleCommandState(event.getCommand());
		
		final IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		proxyService.setProxyScanEnabled(!oldValue);
		return null;
	}
}
