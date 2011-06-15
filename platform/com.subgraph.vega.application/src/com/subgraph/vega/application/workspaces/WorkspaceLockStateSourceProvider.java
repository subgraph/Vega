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
package com.subgraph.vega.application.workspaces;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.WorkspaceLockStatusEvent;
import com.subgraph.vega.application.Activator;

public class WorkspaceLockStateSourceProvider extends AbstractSourceProvider {
	final static String WORKSPACE_LOCK_STATE = "vega.workspaceLockState";
	final static String WORKSPACE_LOCKED = "locked";
	final static String WORKSPACE_UNLOCKED = "unlocked";
	
	private boolean isLocked = false;
	
	public WorkspaceLockStateSourceProvider() {
		final IModel model = Activator.getDefault().getModel();
		model.addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceLockStatusEvent)
					handleWorkspaceLockStatus((WorkspaceLockStatusEvent) event);				
			}
		});		
	}

	private void handleWorkspaceLockStatus(WorkspaceLockStatusEvent event) {
		boolean state = event.isLockEvent();
		if(state != isLocked) {
			isLocked = state;
			fireSourceChanged(ISources.WORKBENCH, WORKSPACE_LOCK_STATE, getCurrentWorkspaceLockState());
		}
	}
	
	
	@Override
	public void dispose() {
	}

	@Override
	synchronized public Map<?,?> getCurrentState() {
		Map<String, String> stateMap = new HashMap<String, String>(1);
		stateMap.put(WORKSPACE_LOCK_STATE, getCurrentWorkspaceLockState());
		return stateMap;
	}

	private String getCurrentWorkspaceLockState() {
		if(isLocked)
			return WORKSPACE_LOCKED;
		else
			return WORKSPACE_UNLOCKED;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { WORKSPACE_LOCK_STATE };
	}

}
