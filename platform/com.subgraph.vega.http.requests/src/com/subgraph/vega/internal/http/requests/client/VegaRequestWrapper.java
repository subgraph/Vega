package com.subgraph.vega.internal.http.requests.client;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.impl.client.RequestWrapper;

public class VegaRequestWrapper extends RequestWrapper implements AbortableHttpRequest {

	public VegaRequestWrapper(HttpRequest request) throws ProtocolException {
		super(request);
	}
	
	@Override
	public RequestLine getRequestLine() {
		return getOriginal().getRequestLine();
	}

	
	private AbortableHttpRequest getAbortableRequest() {
		if(getOriginal() instanceof AbortableHttpRequest) {
			return (AbortableHttpRequest) getOriginal();
		} else {
			return null;
		}
	}
	
	@Override
	public void setConnectionRequest(ClientConnectionRequest connRequest)
			throws IOException {
		final AbortableHttpRequest ab = getAbortableRequest();
		if(ab != null) {
			ab.setConnectionRequest(connRequest);
		}
	}

	@Override
	public void setReleaseTrigger(ConnectionReleaseTrigger releaseTrigger)
			throws IOException {
		final AbortableHttpRequest ab = getAbortableRequest();
		if(ab != null) {
			ab.setReleaseTrigger(releaseTrigger);
		}
	}
	
	@Override
	public void abort() {
		final AbortableHttpRequest ab = getAbortableRequest();
		if(ab != null) {
			ab.abort();
		} else {
			super.abort();
		}
	}
}
