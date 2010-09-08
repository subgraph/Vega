package com.subgraph.vega.internal.requestlog.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.subgraph.vega.api.requestlog.IRequestLogRecord;
import com.subgraph.vega.internal.requestlog.RequestLogId;
import com.subgraph.vega.internal.requestlog.RequestLogRecord;

public class RequestLogMemoryStore {
	
	private Map<Long, RequestLogRecord> records = new LinkedHashMap<Long, RequestLogRecord>();
	
	public RequestLogId getRequestLogId() {
		return null;
	}
	
	public void store(RequestLogId rli) {
	}
	
	public void store(RequestLogRecord record) {
		synchronized(records) {
			if(!records.containsKey(record.getRequestId()))
				records.put(record.getRequestId(), record);
		}
		
	}
	
	public RequestLogRecord lookupRecord(long requestId) {
		synchronized(records) {
			return records.get(requestId);
		}
	}
	
	public Collection<IRequestLogRecord> lookupAllRecords() {
		synchronized(records) {
			List<IRequestLogRecord> list = new ArrayList<IRequestLogRecord>(records.values());
			return Collections.unmodifiableList(list);
		}
	}
	
	public void close() {
		
	}

}
