package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;

public class HostKey {
	private final String hostname;
	private final int port;
	
	HostKey(URI uri) {
		this(uri.getHost(), uri.getPort());
	}
	
	HostKey(String hostname, int port) {
		checkNotNull(hostname);
		checkArgument((port == -1) || (port > 0 && port <= 0xFFFF));
		this.hostname = hostname;
		this.port = (port == -1) ? (80) : (port);
	}
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof HostKey) {
			HostKey that = (HostKey) other;
			return (port == that.port && hostname.equals(that.hostname));
		}
		return false;
	}

	public int hashCode() {
		return Objects.hashCode(hostname, port);
	}
	
	public String toString() {
		return Objects.toStringHelper(this)
			.add("hostname", hostname)
			.add("port", port)
			.toString();
	}
}
