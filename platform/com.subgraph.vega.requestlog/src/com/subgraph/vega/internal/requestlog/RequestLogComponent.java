package com.subgraph.vega.internal.requestlog;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class RequestLogComponent implements IRequestLog {

	private final RequestLog log = new RequestLog("/tmp/requestlog.odb");
	
	@Override
	public long allocateRequestId() {
		return log.allocateRequestId();
	}

	@Override
	public long addRequest(HttpRequest request, HttpHost host) {
		return log.addRequest(request, host);
	}

	@Override
	public void addRequest(long requestId, HttpRequest request, HttpHost host) {
		log.addRequest(requestId, request, host);		
	}
	
	@Override
	public long addRequestResponse(HttpRequest request, HttpResponse response,
			HttpHost host) {
		return log.addRequestResponse(request, response, host);
	}
	
	@Override
	public void addResponse(long requestId, HttpResponse response) {
		log.addResponse(requestId, response);		
	}

	@Override
	public IRequestLogRecord lookupRecord(long requestId) {
		return log.lookupRecord(requestId);
	}

	@Override
	public Iterable<IRequestLogRecord> getAllRecords() {
		return log.getAllRecords();
	}

	@Override
	public void addChangeListenerAndPopulate(IEventHandler listener) {
		log.addChangeListenerAndPopulate(listener);		
	}

	@Override
	public void removeChangeListener(IEventHandler listener) {
		log.removeChangeListener(listener);
	}
}
