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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class ProxyStateSourceProvider extends AbstractSourceProvider {
	public final static String PROXY_STATE = "vega.proxyState";
	final static String PROXY_ENABLED = "enabled";
	final static String PROXY_DISABLED = "disabled";
	private boolean isRunning = false;
	
	@Override
	public void dispose() {		
	}

	@Override
	public Map<?, ?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(3);
		stateMap.put(PROXY_STATE, getCurrentProxyState());
		return stateMap;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { PROXY_STATE };
	}

	public void setProxyRunning(boolean enabled) {
		if (isRunning!= enabled) {
			isRunning = enabled;
			fireSourceChanged(ISources.WORKBENCH, PROXY_STATE, getCurrentProxyState());
		}
	}
	
	private String getCurrentProxyState() {
		if (isRunning) {
			return PROXY_ENABLED;
		} else { 
			return PROXY_DISABLED;
		}
	}
}
