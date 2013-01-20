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

import java.util.ArrayList;
import java.util.List;

public class EventListenerManager {
	private final List<IEventHandler> handlers = new ArrayList<IEventHandler>();

	public void addListener(final IEventHandler listener) {
		synchronized (this) {
			handlers.add(listener);
		}
	}

	public void removeListener(final IEventHandler listener) {
		synchronized (this) {
			handlers.remove(listener);
		}
	}

	public synchronized void clearListeners() {
		handlers.clear();
	}

	public void fireEvent(final IEvent event) {
		IEventHandler[] handlersCopy;

		synchronized (this) {
			handlersCopy = new IEventHandler[handlers.size()];
			handlers.toArray(handlersCopy);
		}
		for (IEventHandler handler : handlersCopy) {
			try {
				handler.handleEvent(event);
			} catch (Exception e) {
				// TODO should log
				e.printStackTrace();
			}
		}
	}

}
