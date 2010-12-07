package com.subgraph.vega.api.model.requests;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.events.IEventHandler;

public interface IRequestLog {
	long allocateRequestId();
	long addRequest(HttpRequest request, HttpHost host);
	void addRequest(long requestId, HttpRequest request, HttpHost host);
	long addRequestResponse(HttpRequest request, HttpResponse response, HttpHost host);
	void addResponse(long requestId, HttpResponse response);
	IRequestLogRecord lookupRecord(long requestId);
	Iterable<IRequestLogRecord> getAllRecords();
	void addChangeListenerAndPopulate(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);
}
