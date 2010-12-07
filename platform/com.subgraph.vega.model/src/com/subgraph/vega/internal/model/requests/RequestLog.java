package com.subgraph.vega.internal.model.requests;

import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.requests.AddRequestRecordEvent;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.RequestRecordChangeEvent;


public class RequestLog implements IRequestLog {
	private final Logger logger = Logger.getLogger("requests");
	private final ObjectContainer database;
	private final RequestLogId requestLogId;
	
	private final EventListenerManager eventManager = new EventListenerManager();
	
	public RequestLog(ObjectContainer database) {
		this.database = database;
		this.requestLogId = getRequestLogId(database);
	}
	
	private RequestLogId getRequestLogId(ObjectContainer database) {
		List<RequestLogId> result = database.query(RequestLogId.class);
		if(result.size() == 0) {
			RequestLogId rli = new RequestLogId();
			database.store(rli);
			return rli;
		} else if(result.size() == 1) {
			return result.get(0);
		} else {
			throw new IllegalStateException("Database corrupted, found multiple RequestLogId instances");
		}
	}
	
	@Override
	public synchronized long allocateRequestId() {
		final long id = requestLogId.allocateId();
		database.store(requestLogId);
		return id;
	}

	
	@Override
	public long addRequest(HttpRequest request, HttpHost host) {
		final long id = allocateRequestId();
		addRequest(id, request, host);
		return id;
	}

	@Override
	public void addRequest(long requestId, HttpRequest request, HttpHost host) {
		final RequestLogRecord record = new RequestLogRecord(requestId, request, host);
		database.store(record);
		eventManager.fireEvent(new AddRequestRecordEvent(record));		
	}

	@Override
	public long addRequestResponse(HttpRequest request, HttpResponse response,
			HttpHost host) {
		final long id = allocateRequestId();
		final RequestLogRecord record = new RequestLogRecord(id, request, response, host);
		database.store(record);
		eventManager.fireEvent(new AddRequestRecordEvent(record));
		return id;
	}

	@Override
	public void addResponse(long requestId, HttpResponse response) {
		final RequestLogRecord record = lookupRecord(requestId);
		if(record == null) {
			logger.warning("Could not find request log record for requestId "+ requestId);
			return;
		}
		record.setResponse(response);
		eventManager.fireEvent(new RequestRecordChangeEvent(record));		
	}

	@Override
	public RequestLogRecord lookupRecord(final long requestId) {
		List<RequestLogRecord> result = database.query(new Predicate<RequestLogRecord>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(RequestLogRecord record) {
				return record.getRequestId() == requestId;	
			}
		});
		
		if(result.size() == 0)
			return null;
		else if(result.size() == 1)
			return result.get(0);
		else
			throw new IllegalStateException("Database corrupted, found multiple RequestLogRecords for id == "+ requestId);
	}

	@Override
	public Iterable<IRequestLogRecord> getAllRecords() {
		return database.query(IRequestLogRecord.class);
	}

	@Override
	public void addChangeListenerAndPopulate(IEventHandler listener) {
		for(IRequestLogRecord record: getAllRecords())
			listener.handleEvent(new AddRequestRecordEvent(record));
		eventManager.addListener(listener);		
	}

	@Override
	public void removeChangeListener(IEventHandler listener) {
		eventManager.removeListener(listener);		
	}
}
