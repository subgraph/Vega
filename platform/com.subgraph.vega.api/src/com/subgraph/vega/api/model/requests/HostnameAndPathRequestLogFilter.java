package com.subgraph.vega.api.model.requests;

import java.net.URI;

public class HostnameAndPathRequestLogFilter implements IRequestLogFilter {
	private final String hostname;
	private final String path;

	public HostnameAndPathRequestLogFilter(String hostname, String path) {
		this.hostname = hostname;
		this.path = path;
	}

	@Override
	public boolean match(IRequestLogRecord record) {
		final URI uri = URI.create(record.getRequest().getRequestLine().getUri());
		final String hname = record.getHttpHost().getHostName();
		return hostname.equals(hname) && uri.getPath().startsWith(path);
	}

	public String getHostname() {
		return hostname;
	}

	public String getPath() {
		return path;
	}

}
