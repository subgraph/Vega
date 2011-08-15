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

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.ui.http.Activator;

public class StopProxyHandler extends AbstractProxyCommandHandler {
	@Override
	protected void executeCommand(IHttpProxyService proxyService, ProxyStateSourceProvider proxyState) {
		proxyService.stop();
		proxyState.setProxyRunning(false);
	}

}
