package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;

public class WebHost extends AbstractWebEntity implements IWebHost {
	private final static int DEFAULT_HTTP_PORT = 80;
	private final static int DEFAULT_HTTPS_PORT = 443;
	
	private final String hostname;
	private final URI uri;
	private Set<InetAddress> addresses = new LinkedHashSet<InetAddress>();
	private final int port;
	private final WebPath rootPath;
	
	public WebHost(WebModel model, String hostname, int port, boolean ssl) {
		super(model);
		checkArgument((port == -1) || (port > 0 && port <= 0xFFFF));
		this.hostname = checkNotNull(hostname);;
		this.port = mapUndefinedPort(port, ssl);
		this.uri = createURI(hostname, port, ssl);
		this.rootPath = new WebPath(model, this, "", null);
	}
	
	private static int mapUndefinedPort(int port, boolean ssl) {
		if(port == -1)
			return (ssl) ? DEFAULT_HTTPS_PORT : DEFAULT_HTTP_PORT;
		else 
			return port;
	}
	
	private URI createURI(String hostname, int port, boolean ssl) {
			try {
				if(ssl)
					return createSecureURI(hostname, port);
				else
					return createCommonURI(hostname, port);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException("Failed to create host URI for hostname: "+ hostname, e);
			}
	}
	
	private URI createCommonURI(String hostname, int port) throws URISyntaxException {
		if(port == -1 || port == DEFAULT_HTTP_PORT)
			return new URI("http", hostname, null, null);
		else
			return new URI("http", null, hostname, port, null, null, null);
	}
	
	private URI createSecureURI(String hostname, int port) throws URISyntaxException {
		if(port == -1 || port == DEFAULT_HTTPS_PORT) 
			return new URI("https", hostname, null, null);
		else
			return new URI("https", null, hostname, port, null, null,null);
	}
	
	public WebHost(WebModel model, String hostname, InetAddress address, int port, boolean ssl) {
		this(model, hostname, port, ssl);
		setAddress(address, false);
	}
	
	@Override public String getHostname() { return hostname; }
	@Override public int getPort() { return port; }
	@Override public IWebEntity getParent() { return null; }
	@Override public URI toURI() { return uri; }
	@Override public IWebHost getHostEntity() { return this; }
	@Override public IWebPath getRootPath() { return rootPath; }

	@Override public Iterable<InetAddress> getAddresses() { 
		synchronized (addresses) {
			return Iterables.unmodifiableIterable(addresses);	
		}
	}
	
	@Override
	public void setAddress(InetAddress address, boolean notify) {
		synchronized (addresses) {	
			if(addresses.contains(address))
				return;
			addresses.add(address);
		}
		if(notify)
			model.notifyEntityChanged(this);
	}

	@Override
	public IWebPath addPath(String path) {
		IWebPath wp = rootPath;
		for(String s : path.split("/")) {
			if(!s.isEmpty())
				wp = wp.getOrCreateChildPath(s);
		}
		return wp;
	}
	
	@Override public String toString() {
		return Objects.toStringHelper(this).add("hostname", hostname).toString();
	}
	
	@Override 
	public boolean equals(Object other) {
		if(this == other)
			return true;	
		if(other instanceof WebHost) {
			WebHost that = (WebHost) other;
			return hostname.equals(that.hostname);
		}
		return false;
	}
	
	@Override public int hashCode() {
		return hostname.hashCode();
	}

}
