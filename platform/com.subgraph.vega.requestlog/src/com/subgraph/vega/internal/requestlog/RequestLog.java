package com.subgraph.vega.internal.requestlog;

import java.net.InetAddress;
import java.util.Collection;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.requestlog.IRequestLog;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;
import com.subgraph.vega.internal.requestlog.store.RequestLogMemoryStore;

public class RequestLog implements IRequestLog {
	
	
	private static RequestLogId initializeRequestLogId(RequestLogMemoryStore store) {
		RequestLogId rli = store.getRequestLogId();
		if(rli == null) {
			rli = new RequestLogId();
			store.store(rli);
		}
		return rli;
	}
	
	private final RequestLogMemoryStore store = new RequestLogMemoryStore();
	private final RequestLogId requestLogId;
	private final EventListenerManager eventManager = new EventListenerManager();
	
	RequestLog(String dbPath) {
		requestLogId = initializeRequestLogId(store);
	}
	
	public long addRequest(HttpRequest request, HttpHost host, InetAddress address) {
		final long id = allocateRequestId();
		addRequest(id, request, host, address);
		return id;
	}
	
	public void addRequest(long requestId, HttpRequest request, HttpHost host, InetAddress address) {
		final RequestLogRecord record = new RequestLogRecord(requestId, request, host, address);
		store.store(record);
		eventManager.fireEvent(new AddRecordEvent(record));
	}
	
	@Override
	public long addRequestResponse(HttpRequest request, HttpResponse response,
			HttpHost host, InetAddress address) {
		final long id = allocateRequestId();
		final RequestLogRecord record = new RequestLogRecord(id, request, response, host, address);
		store.store(record);
		eventManager.fireEvent(new AddRecordEvent(record));
		return id;
	}
	public void addResponse(long requestId, HttpResponse response) {
		final RequestLogRecord record = lookupRecord(requestId);
		if(record == null)
			throw new IllegalStateException("Record not found for requestId = "+ requestId);
		record.setResponse(response);
		store.store(record);
		eventManager.fireEvent(new RecordChangeEvent(record));
	}
	
	public long allocateRequestId() {
		final long ret =  requestLogId.allocateId();
		store.store(requestLogId);
		return ret;
	}
	
	public RequestLogRecord lookupRecord(long requestId) {
		 return store.lookupRecord(requestId);
	}
	 
	public Collection<IRequestLogRecord> getAllRecords() {
		return store.lookupAllRecords();
	}
	
	void close() {
		store.close();
	}

	@Override
	public void addChangeListenerAndPopulate(IEventHandler listener) {
		for(IRequestLogRecord record: getAllRecords()) 
			listener.handleEvent(new AddRecordEvent(record));
		eventManager.addListener(listener);		
	}

	@Override
	public void removeChangeListener(IEventHandler listener) {
		eventManager.removeListener(listener);		
	}
}
