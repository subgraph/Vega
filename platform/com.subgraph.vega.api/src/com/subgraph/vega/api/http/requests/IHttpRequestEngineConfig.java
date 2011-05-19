package com.subgraph.vega.api.http.requests;

import java.util.List;

import org.apache.http.client.CookieStore;

public interface IHttpRequestEngineConfig {
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
	CookieStore getCookieStore();
}
