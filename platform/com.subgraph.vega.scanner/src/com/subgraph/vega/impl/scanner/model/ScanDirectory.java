package com.subgraph.vega.impl.scanner.model;

import java.net.URI;

import com.google.common.base.Objects;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanHost;

public class ScanDirectory extends AbstractScanEntity implements IScanDirectory {

	private final IScanHost host;
	
	ScanDirectory(IScanHost host, URI uri) {
		super(uri);
		this.host = host;
	}

	@Override
	public IScanHost getHost() {
		return host;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("host", host).add("uri", getURI()).toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof ScanDirectory) {
			ScanDirectory that = (ScanDirectory) other;
			return host.equals(that.host) && getURI().equals(that.getURI());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(host, getURI());
	}

}
