package com.subgraph.vega.internal.http.requests.client;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;

public class VegaEntityEnclosingRequestWrapper extends EntityEnclosingRequestWrapper {

	public VegaEntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request)
			throws ProtocolException {
		super(request);
	}

	@Override
	public RequestLine getRequestLine() {
		return getOriginal().getRequestLine();
	}
}
