package com.subgraph.vega.internal.model.requests;

import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import com.db4o.ObjectContainer;

public class RequestLogEntityEnclosingRequest extends BasicHttpEntityEnclosingRequest {

	private final long entityId;
	private transient ObjectContainer database;

	public RequestLogEntityEnclosingRequest(ObjectContainer database, RequestLine requestline, long entityId) {
		super(requestline);
		this.entityId = entityId;
		this.database = database;
	}

	void setDatabase(ObjectContainer database) {
		this.database = database;
	}

	@Override
	public HttpEntity getEntity() {
		if(entityId == 0)
			return null;
		final HttpEntity e = database.ext().getByID(entityId);
		database.ext().activate(e);
		return e;
	}

	@Override
	public void setEntity(final HttpEntity entity) {
		throw new UnsupportedOperationException();
	}
}
