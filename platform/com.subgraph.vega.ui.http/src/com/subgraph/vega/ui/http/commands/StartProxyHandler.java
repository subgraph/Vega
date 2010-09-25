package com.subgraph.vega.ui.http.commands;

import java.util.logging.Logger;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;

public class StartProxyHandler extends AbstractProxyCommandHandler {

	@Override
	protected void executeCommand(IHttpProxyService proxyService,
			ProxyStateSourceProvider proxyState) {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Start proxy");
		proxyService.start();
		proxyState.setProxyStarted();		
	}
}
