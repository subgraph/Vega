/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.model.requests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.ObjectContainer;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectInfoEventArgs;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.internal.model.conditions.HttpConditionSet;


public class RequestLog implements IRequestLog {
	private final ObjectContainer database;
	private final RequestLogId requestLogId;
	private final HttpMessageCloner cloner;
	private final List<RequestLogListener> listenerList = new ArrayList<RequestLogListener>();
	private final Object lock = new Object();

	public RequestLog(final ObjectContainer database) {
		this.database = database;
		this.requestLogId = getRequestLogId(database);
		this.cloner = new HttpMessageCloner(database);
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(database);
		
		registry.activated().addListener(new EventListener4<ObjectInfoEventArgs> () {

			@Override
			public void onEvent(Event4<ObjectInfoEventArgs> arg0,  ObjectInfoEventArgs args) {
				final Object ob = args.object();
				if(ob instanceof RequestLogResponse) {
					final RequestLogResponse r = (RequestLogResponse) ob;
					r.setDatabase(database);
				} else if(ob instanceof RequestLogEntityEnclosingRequest) {
					final RequestLogEntityEnclosingRequest r = (RequestLogEntityEnclosingRequest) ob;
					r.setDatabase(database);
				} 
			}
		});
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
	public long allocateRequestId() {
		final long id = requestLogId.allocateId();
		database.store(requestLogId);
		return id;
	}

	@Override
	public long addRequestResponse(IHttpResponse response) {
		if (response.getRequestId() != -1) {
			return response.getRequestId();
		}
		final long id = allocateRequestId();
		final HttpRequest newRequest = cloner.copyRequest(response.getOriginalRequest());
		final HttpResponse newResponse = cloner.copyResponse(response.getRawResponse());
		database.store(newRequest);
		database.store(newResponse);
		final RequestLogRecord record = new RequestLogRecord(id, newRequest, newResponse, response.getHost(), response.getRequestMilliseconds(), response.getTags());
		synchronized(lock){
			database.store(record);
			filterNewRecord(record);
		}
		response.setRequestId(id);
		return id;
	}
	
	private void filterNewRecord(IRequestLogRecord record) {
		for(RequestLogListener listener: listenerList) {
			listener.filterRecord(record);
		}
	}

	@Override
	public RequestLogRecord lookupRecord(final long requestId) {
		synchronized(this) {
			List<RequestLogRecord> result = database.query(new Predicate<RequestLogRecord>() {
				private static final long serialVersionUID = 1L;
				@Override
				public boolean match(RequestLogRecord record) {
					return record.requestId == requestId;
				}
			});

			if(result.size() == 0)
				return null;
			else if(result.size() == 1)
				return result.get(0);
			else
				throw new IllegalStateException("Database corrupted, found multiple RequestLogRecords for id == "+ requestId);
		}
	}

	@Override
	public List<IRequestLogRecord> getAllRecords() {
		if(!hasRecords()) {
			return Collections.emptyList();
		}
		final Query query = database.query();
		query.constrain(IRequestLogRecord.class);
		return query.execute();
	}
	
	private boolean hasRecords() {
		final Query query = database.query();
		query.constrain(IRequestLogRecord.class);
		return query.execute().size() > 0;
	}

	public List<IRequestLogRecord> getRecordsByConditionSet(IHttpConditionSet conditionFilter) {
		if(conditionFilter instanceof HttpConditionSet) {
			return ((HttpConditionSet)conditionFilter).filterRequestLog(database);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void addUpdateListener(IRequestLogUpdateListener callback) {
		synchronized(lock) {
			listenerList.add(new RequestLogListener(callback, null, getAllRecords().size()));
		}
	}

	@Override
	public void addUpdateListener(IRequestLogUpdateListener callback, IHttpConditionSet conditionFilter) {
		synchronized(lock) {
			listenerList.add(new RequestLogListener(callback, conditionFilter, getRecordsByConditionSet(conditionFilter).size()));
		}
	}

	@Override
	public void removeUpdateListener(IRequestLogUpdateListener callback) {
		synchronized (lock) {
			final Iterator<RequestLogListener> it = listenerList.iterator();
			while(it.hasNext()) {
				RequestLogListener listener = it.next();
				if(listener.getListenerCallback() == callback)
					it.remove();
			}
		}
	}
}
