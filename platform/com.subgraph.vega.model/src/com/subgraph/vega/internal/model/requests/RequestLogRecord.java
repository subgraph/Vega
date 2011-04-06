package com.subgraph.vega.internal.model.requests;

import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;


public class RequestLogRecord implements IRequestLogRecord, Activatable {
	final long requestId;
	private final HttpRequest request;
	private final HttpHost host;
	private HttpResponse response;
	private final long timestamp;

	private transient Activator activator;

	RequestLogRecord() {
		requestId = 0;
		request = null;
		response = null;
		host = null;
		timestamp = 0;
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpResponse response, HttpHost host) {
		this.requestId = requestId;
		this.request = request;
		this.response = response;
		this.host = host;
		this.timestamp = new Date().getTime();
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpHost host) {
		this(requestId, request, null, host);
	}

	void setResponse(HttpResponse response) {
		activate(ActivationPurpose.WRITE);
		this.response = response;
	}

	@Override
	public long getRequestId() {
		activate(ActivationPurpose.READ);
		return requestId;
	}

	@Override
	public long getTimestamp() {
		activate(ActivationPurpose.READ);
		return timestamp;
	}

	@Override
	public HttpRequest getRequest() {
		activate(ActivationPurpose.READ);
		return request;
	}

	@Override
	public HttpResponse getResponse() {
		activate(ActivationPurpose.READ);
		synchronized(response) {
			return response;
		}
	}

	@Override
	public HttpHost getHttpHost() {
		activate(ActivationPurpose.READ);
		return host;
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}

		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}

		this.activator = activator;
	}
}
