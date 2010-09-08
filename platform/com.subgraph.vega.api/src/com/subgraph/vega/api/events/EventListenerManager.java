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
