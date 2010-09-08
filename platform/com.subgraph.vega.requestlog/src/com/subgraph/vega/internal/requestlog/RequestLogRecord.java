package com.subgraph.vega.internal.requestlog;

import java.net.InetAddress;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class RequestLogRecord implements IRequestLogRecord {
	private final long requestId;
	private final HttpRequest request;
	private final HttpHost host;
	private final InetAddress address;
	private HttpResponse response; 
	private final long timestamp = new Date().getTime();
	
	RequestLogRecord(long requestId, HttpRequest request, HttpResponse response, HttpHost host, InetAddress address) {
		this.requestId = requestId;
		this.request = request;
		this.response = response;
		this.host = host;
		this.address = address;
	}
	
	RequestLogRecord(long requestId, HttpRequest request, HttpHost host, InetAddress address) {
		this.requestId = requestId;
		this.request = request;
		this.host = host;
		this.address = address;
	}
	
	void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	@Override
	public long getRequestId() {
		return requestId;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}

	@Override
	public HttpResponse getResponse() {
		return response;
	}

	@Override
	public HttpHost getHttpHost() {
		return host;
	}

	@Override
	public InetAddress getAddress() {
		return address;
	}
}
