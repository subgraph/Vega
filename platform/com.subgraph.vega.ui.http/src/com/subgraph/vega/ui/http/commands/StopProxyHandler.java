package com.subgraph.vega.ui.http.commands;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;

public class StopProxyHandler extends AbstractProxyCommandHandler {
	@Override
	protected void executeCommand(IHttpProxyService proxyService,
			ProxyStateSourceProvider proxyState) {
		proxyService.stop();
		proxyState.setProxyRunning(false);
		Activator.getDefault().setStatusLineProxyStopped();
	}

}
