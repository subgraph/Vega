package com.subgraph.vega.internal.http.requests.client;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.impl.client.RequestWrapper;

public class VegaRequestWrapper extends RequestWrapper {

	public VegaRequestWrapper(HttpRequest request) throws ProtocolException {
		super(request);
	}
	
	@Override
	public RequestLine getRequestLine() {
		return getOriginal().getRequestLine();
	}

}
