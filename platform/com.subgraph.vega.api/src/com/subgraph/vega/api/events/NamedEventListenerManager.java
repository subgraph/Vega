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
package com.subgraph.vega.api.events;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NamedEventListenerManager {
	
	private final Map<String, EventListenerManager> eventManagerMap = new HashMap<String, EventListenerManager>();
	
	public void addListener(String key, IEventHandler listener) {
		getEventManagerByName(key).addListener(listener);
	}
	
	public void fireEvent(String key, final IEvent event) {
		getEventManagerByName(key).fireEvent(event);
	}

	public void fireAllKeys(IEvent event) {
		synchronized (eventManagerMap) {
			for(String key: eventManagerMap.keySet()) 
				eventManagerMap.get(key).fireEvent(event);
		}
	}

	public void removeListener(String name, IEventHandler listener) {
		getEventManagerByName(name).removeListener(listener);
	}
	
	public void removeListener(IEventHandler listener) {
		synchronized(eventManagerMap) {
			for(EventListenerManager manager: eventManagerMap.values()) 
				manager.removeListener(listener);
		}
	}
	
	public Collection<String> getAllKeys() {
		return eventManagerMap.keySet();
	}
	
	private EventListenerManager getEventManagerByName(String key) {
		synchronized(eventManagerMap) {
			if(!eventManagerMap.containsKey(key))
				eventManagerMap.put(key, new EventListenerManager());
			return eventManagerMap.get(key);
		}
	}
}
