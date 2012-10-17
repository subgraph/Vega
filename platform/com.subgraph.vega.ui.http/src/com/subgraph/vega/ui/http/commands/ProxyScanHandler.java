package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;

public class ProxyScanHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
				
		final IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		final boolean newValue = !proxyService.isProxyScanEnabled();
		
		proxyService.setProxyScanEnabled(newValue);
		getStateSourceProvider(event).setProxyScan(newValue);
		return null;
	}
	
	private ProxyStateSourceProvider getStateSourceProvider(ExecutionEvent event) {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		final ISourceProviderService service = (ISourceProviderService) window.getService(ISourceProviderService.class);
		return (ProxyStateSourceProvider) service.getSourceProvider(ProxyStateSourceProvider.PROXYSCAN_STATE);
	}
}
