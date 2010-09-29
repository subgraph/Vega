package com.subgraph.vega.impl.scanner.model;

import java.net.URI;

import com.google.common.base.Objects;
import com.subgraph.vega.api.scanner.model.IScanHost;

public class ScanHost extends AbstractScanEntity implements IScanHost {
	ScanHost(URI uri) {
		super(uri);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("uri", getURI()).toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof ScanHost) {
			ScanHost that = (ScanHost) other;
			return getURI().equals(that.getURI());
		}
		return false;
	}
	
	@Override public int hashCode() {
		return getURI().hashCode();
	}
}
