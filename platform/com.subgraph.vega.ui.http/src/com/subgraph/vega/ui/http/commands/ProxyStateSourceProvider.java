package com.subgraph.vega.ui.http.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class ProxyStateSourceProvider extends AbstractSourceProvider {
	final static String PROXY_STATE = "vega.proxyState";
	final static String PROXY_STARTED = "started";
	final static String PROXY_STOPPED = "stopped";
	
	private boolean isStarted = false;
	
	@Override
	public void dispose() {		
	}

	@Override
	public Map<?, ?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(PROXY_STATE, getCurrentProxyState());
		return stateMap;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { PROXY_STATE };
	}

	void setProxyStarted() {
		setProxyState(true);
	}
	
	void setProxyStopped() {
		setProxyState(false);
	}
	
	private void setProxyState(boolean state) {
		if(state != isStarted) {
			isStarted = state;
			fireSourceChanged(ISources.WORKBENCH, PROXY_STATE, getCurrentProxyState());
		}
	}
	
	private String getCurrentProxyState() {
		if(isStarted)
			return PROXY_STARTED;
		else 
			return PROXY_STOPPED;
	}
}
