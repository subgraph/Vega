package com.subgraph.vega.ui.http.commands;

import com.subgraph.vega.http.proxy.IHttpProxyService;

public class StopProxyHandler extends AbstractProxyCommandHandler {
	@Override
	protected void executeCommand(IHttpProxyService proxyService,
			ProxyStateSourceProvider proxyState) {
		proxyService.stop();
		proxyState.setProxyStopped();		
	}

}
