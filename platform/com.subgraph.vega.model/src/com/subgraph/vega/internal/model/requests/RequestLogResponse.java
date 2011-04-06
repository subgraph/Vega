package com.subgraph.vega.internal.model.requests;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;

import com.db4o.ObjectContainer;

public class RequestLogResponse extends BasicHttpResponse {

	private final long entityId;
	private transient ObjectContainer database;

	public RequestLogResponse(ObjectContainer database, StatusLine statusline, long entityId) {
		super(statusline);
		this.database = database;
		this.entityId = entityId;
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
