package com.subgraph.vega.api.model.requests;

public class HostnameRequestLogFilter implements IRequestLogFilter {
	private final String hostname;

	public HostnameRequestLogFilter(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public boolean match(IRequestLogRecord record) {
		return hostname.equals(record.getHttpHost().getHostName());
	}

	public String getHostname() {
		return hostname;
	}

}
