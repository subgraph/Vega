package com.subgraph.vega.ui.http.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class InterceptStateSourceProvider extends AbstractSourceProvider {
	public final static String INTERCEPT_STATE = "vega.interceptState";
	final static String INTERCEPT_PENDING = "pending";
	final static String INTERCEPT_IDLE = "empty";
	private boolean isPending = false;

	@Override
	public void dispose() {
	}

	@Override
	public Map<?, ?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(INTERCEPT_STATE, getCurrentPendingState());
		return stateMap;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { INTERCEPT_STATE };
	}

	public void setInterceptPending() {
		setInterceptState(true);
	}
	
	public void setInterceptEmpty() {
		setInterceptState(false);
	}

	private void setInterceptState(boolean state) {
		if (state != isPending) {
			isPending = state;
			fireSourceChanged(ISources.WORKBENCH, INTERCEPT_STATE, getCurrentPendingState());
		}
	}

	private String getCurrentPendingState() {
		if (isPending) {
			return INTERCEPT_PENDING;
		} else { 
			return INTERCEPT_IDLE;
		}
	}

}
