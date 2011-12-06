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

public class InterceptQueueStateSourceProvider extends AbstractSourceProvider {
	public static final String INTERCEPT_QUEUE_STATE = "vega.interceptQueueState";
	public static final String QUEUE_PENDING = "pending";
	public static final String QUEUE_EMPTY = "empty";
	private boolean isPending = false;

	@Override
	public void dispose() {
	}

	@Override
	public Map<?, ?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(INTERCEPT_QUEUE_STATE, getCurrentQueueState());
		return stateMap;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { INTERCEPT_QUEUE_STATE, };
	}

	private String getCurrentQueueState() {
		if (isPending == true) {
			return QUEUE_PENDING;
		} else {
			return QUEUE_EMPTY;
		}
	}

	public void setPending(boolean isPending) {
		this.isPending = isPending;
		fireSourceChanged(ISources.WORKBENCH, INTERCEPT_QUEUE_STATE, getCurrentQueueState());
	}
	
}
