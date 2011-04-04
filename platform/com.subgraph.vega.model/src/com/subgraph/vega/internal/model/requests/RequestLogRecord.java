package com.subgraph.vega.internal.model.requests;

import java.io.IOException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;


public class RequestLogRecord implements IRequestLogRecord, Activatable {
	private final long requestId;
	private final HttpRequest request;
	private final HttpHost host;
	private HttpResponse response;
	private final long timestamp;
	
	private transient Activator activator;
	
	
	private static HttpRequest copyRequest(HttpRequest request) {
		if(request == null)
			return null;
		if(request instanceof HttpEntityEnclosingRequest) 
			return copyEntityEnclosingRequest((HttpEntityEnclosingRequest) request);
		
		final HttpRequest newRequest = new BasicHttpRequest(request.getRequestLine());
		copyHeaders(request, newRequest);
		return newRequest;
	}
	
	private static HttpRequest copyEntityEnclosingRequest(HttpEntityEnclosingRequest request) {
		final HttpEntityEnclosingRequest newRequest = new BasicHttpEntityEnclosingRequest(request.getRequestLine());
		final HttpEntity entity = request.getEntity();
		if(entity != null)
			newRequest.setEntity(createEntityCopy(entity));
		copyHeaders(request, newRequest);
		return newRequest;
	}
	
	
	private static HttpResponse copyResponseHeader(HttpResponse response) {
		if(response == null)
			return null;
		final HttpResponse newResponse = new BasicHttpResponse(response.getStatusLine());
		copyHeaders(response, newResponse);
		return newResponse;
	}
	
	private static HttpEntity createEntityCopy(HttpEntity entity) {
		try {
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentEncoding(entity.getContentEncoding());
			newEntity.setContentType(entity.getContentType());
			return newEntity;
		} catch (IOException e) {
			return null;
		}
	}
	private static void copyHeaders(HttpMessage source, HttpMessage target) {
		for(Header h: source.getAllHeaders())
			target.addHeader(new BasicHeader(h.getName(), h.getValue()));
	}
	
	RequestLogRecord() {
		requestId = 0;
		request = null;
		response = null;
		host = null;
		timestamp = 0;
	}

	RequestLogRecord(long requestId, HttpRequest request, HttpResponse response, HttpHost host) {
		this.requestId = requestId;
		this.request = copyRequest(request);
		
		this.response = copyResponseHeader(response);
		if(response.getEntity() != null) {
			final HttpEntity e = createEntityCopy(response.getEntity());
			if(e != null) {
				this.response.setEntity(e);
			}
		}
		this.host = host;
		this.timestamp = new Date().getTime();
	}
	
	RequestLogRecord(long requestId, HttpRequest request, HttpHost host) {
		this(requestId, request, null, host);
	}
	
	void setResponse(HttpResponse response) {
		activate(ActivationPurpose.WRITE);
		synchronized(response) {
			this.response = copyResponseHeader(response);
		}
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
