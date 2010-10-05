package com.subgraph.vega.api.http.requests;

import java.util.List;

public interface IHttpRequestEngineConfig {
	void setForceIdentityEncoding(boolean value);
	void setDecompressGzipEncoding(boolean value);
	boolean getForceIdentityEncoding();
	boolean getDecompressGzipEncoding();
	void registerResponseProcessor(IHttpResponseProcessor processor);
	List<IHttpResponseProcessor> getResponseProcessors();
}
