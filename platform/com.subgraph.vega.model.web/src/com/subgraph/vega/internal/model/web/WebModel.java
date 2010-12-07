package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.web.old.IWebEntity;
import com.subgraph.vega.api.model.web.old.IWebHost;
import com.subgraph.vega.api.model.web.old.IWebModel;
import com.subgraph.vega.api.model.web.old.IWebPath;

public class WebModel implements IWebModel {
	
	private final EventListenerManager eventManager = new EventListenerManager();
	private final Map<HostKey, IWebHost> webhostsByKey = new LinkedHashMap<HostKey, IWebHost>();
	private final Set<IWebHost> webhosts = new LinkedHashSet<IWebHost>();
	
	void notifyEntityAdded(IWebEntity entity) {
		eventManager.fireEvent(new AddEntityEvent(entity));
	}
	
	void notifyEntityChanged(IWebEntity entity) {
		eventManager.fireEvent(new EntityChangeEvent(entity));
	}

	@Override
	public void addChangeListenerAndPopulate(IEventHandler listener) {
		for(IWebHost wh: webhostsByKey.values()) 
			listener.handleEvent(new AddEntityEvent(wh));
		eventManager.addListener(listener);		
	}

	@Override
	public void removeChangeListener(IEventHandler listener) {
		eventManager.removeListener(listener);		
	}

	@Override
	public IWebHost getWebHostByNameAndPort(String name, int port) {
		return webhostsByKey.get(new HostKey(name, port));
	}

	@Override
	public IWebHost addWebHost(String name, int port, boolean isSSL) {
		
		final IWebHost host = getWebHostByNameAndPort(name, port);
		if(host != null) 
			return host;
		else 
			return createWebHost(name, port, true, isSSL);
	}

	@Override
	public IWebPath addURI(URI uri) {
		checkNotNull(uri);
		final HostKey key = new HostKey(uri);
		synchronized(webhostsByKey) {
			if(webhostsByKey.containsKey(key)) {
				IWebHost hostEntity = webhostsByKey.get(key);
				return hostEntity.addPath(uri.getPath());
			}
		}
		boolean isSSL = uri.getScheme().equals("https");
		IWebHost hostEntity = createWebHost(uri.getHost(), uri.getPort(), false, isSSL);
		return hostEntity.addPath(uri.getPath());
	}
	
	private IWebHost createWebHost(String name, int port, boolean isVisited, boolean isSSL) {
		IWebHost wh = new WebHost(this, name, port, isSSL);
		synchronized (webhostsByKey) {
			webhostsByKey.put(new HostKey(name, port), wh);
			webhosts.add(wh);
		}
		if(isVisited)
			wh.setVisited(false);
		notifyEntityAdded(wh);
		return wh;
	}

}
