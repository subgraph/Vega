package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;

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
	public IWebHost addWebHost(String name, InetAddress address, int port,
			boolean isSSL) {
		
		final IWebHost host = getWebHostByNameAndPort(name, port);
		if(host != null) {
			host.setAddress(address, true);
			return host;
		} else {
			return createWebHost(name, address, port, true, isSSL);
		}
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
		IWebHost hostEntity = createWebHost(uri.getHost(), null, uri.getPort(), false, isSSL);
		return hostEntity.addPath(uri.getPath());
	}
	
	private IWebHost createWebHost(String name, InetAddress address, int port, boolean isVisited, boolean isSSL) {
		IWebHost wh = new WebHost(this, name, address, port, isSSL);
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
