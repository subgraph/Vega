package com.subgraph.vega.internal.http.requests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpResponseProcessor;

public class HttpRequestEngineConfig implements IHttpRequestEngineConfig {

	private boolean forceIdentityEncoding = false;
	private boolean decompressGzipEncoding = true;
	private final List<IHttpResponseProcessor> responseProcessors = new ArrayList<IHttpResponseProcessor>();

	@Override
	public void setForceIdentityEncoding(boolean value) {
		forceIdentityEncoding = value;		
	}

	@Override
	public void setDecompressGzipEncoding(boolean value) {
		decompressGzipEncoding = value;		
	}

	@Override
	public boolean getForceIdentityEncoding() {
		return forceIdentityEncoding;
	}

	@Override
	public boolean getDecompressGzipEncoding() {
		return decompressGzipEncoding;
	}

	@Override
	public void registerResponseProcessor(IHttpResponseProcessor processor) {
		responseProcessors.add(processor);		
	}

	@Override
	public List<IHttpResponseProcessor> getResponseProcessors() {
		return Collections.unmodifiableList(responseProcessors);
	}
}
