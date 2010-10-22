package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;

abstract class AbstractProxyCommandHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) {
		IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		if(proxyService == null) {
			// XXX 
			System.out.println("Proxy service is null :(");
			return null;
		}
		
		ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil.getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
		ProxyStateSourceProvider proxyState = (ProxyStateSourceProvider) sourceProviderService.getSourceProvider(ProxyStateSourceProvider.PROXY_STATE);
		if(proxyState == null) {
			System.out.println("proxystate is null :(");
			return null;
		}
		executeCommand(proxyService, proxyState);
		
		return null;
		
	}
	
	abstract protected void executeCommand(IHttpProxyService proxyService, ProxyStateSourceProvider proxyState);
	

}
