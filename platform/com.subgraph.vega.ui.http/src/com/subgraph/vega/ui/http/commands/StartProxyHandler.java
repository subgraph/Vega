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

import java.util.logging.Logger;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;

public class StartProxyHandler extends AbstractProxyCommandHandler {

	@Override
	protected void executeCommand(IHttpProxyService proxyService, ProxyStateSourceProvider proxyState) {
		Logger logger = Logger.getLogger(getClass().getName());
		logger.info("Start proxy");
		// REVISIT: pop up warning if proxy has no listeners configured?
		proxyService.start();
		proxyState.setProxyRunning(true);
	}
}
