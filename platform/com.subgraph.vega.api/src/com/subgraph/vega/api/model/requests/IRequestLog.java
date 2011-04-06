package com.subgraph.vega.api.model.requests;

import java.util.List;

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
	List<IRequestLogRecord> getAllRecords();
	List<IRequestLogRecord> getAllRecordsByFilter(IRequestLogFilter filter);
	void addChangeListener(IEventHandler listener);
	void removeChangeListener(IEventHandler listener);

	void addUpdateListener(IRequestLogUpdateListener callback);
	void addUpdateListener(IRequestLogUpdateListener callback, IRequestLogFilter filter);
	void removeUpdateListener(IRequestLogUpdateListener callback);

}
