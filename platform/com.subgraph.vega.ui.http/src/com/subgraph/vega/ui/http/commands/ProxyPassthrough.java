package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.ErrorDisplay;

public class ProxyPassthrough extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		if(proxyService == null) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDisplay.displayError(shell, "Proxy service is null");
			return null;
		}
		
		ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil.getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
		ProxyStateSourceProvider proxyState = (ProxyStateSourceProvider) sourceProviderService.getSourceProvider(ProxyStateSourceProvider.PASSTHROUGH_STATE);
		if(proxyState == null) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ErrorDisplay.displayError(shell, "Proxy state provider is null");
			return null;
		}

		final boolean toggle = !proxyService.isPassthrough();
		proxyService.setPassthrough(toggle);
		proxyState.setProxyPassthrough(toggle);
		
		return null;
	}

}
