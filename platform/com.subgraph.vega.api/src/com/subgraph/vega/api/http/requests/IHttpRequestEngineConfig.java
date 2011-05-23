package com.subgraph.vega.api.http.requests;

import java.util.List;

import org.apache.http.client.CookieStore;

public interface IHttpRequestEngineConfig {
	final static int DEFAULT_MAX_CONNECTIONS = 10;
	final static int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
	void setForceIdentityEncoding(boolean value);
	void setDecompressGzipEncoding(boolean value);
	void setUndoURLEncoding(boolean value);
	boolean getForceIdentityEncoding();
	boolean getDecompressGzipEncoding();
	boolean getUndoURLEncoding();
	void registerResponseProcessor(IHttpResponseProcessor processor);
	List<IHttpResponseProcessor> getResponseProcessors();
	void setRequestsPerMinute(int rpm);
	int getRequestsPerMinute();
	void setMaxConnections(int value);
	int getMaxConnections();
	void setMaxConnectionsPerRoute(int value);
	int getMaxConnectionsPerRoute();
	CookieStore getCookieStore();
}
