package com.subgraph.vega.internal.model.requests;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;

import com.db4o.ObjectContainer;

public class RequestLogResponse extends BasicHttpResponse {

	private final long entityId;

	private transient LazyEntityLoader loader;

	public RequestLogResponse(ObjectContainer database, StatusLine statusline, long entityId) {
		super(statusline);
		this.entityId = entityId;
		setDatabase(database);
	}
	
	void setDatabase(ObjectContainer database) {
		this.loader = new LazyEntityLoader(entityId, database);
	}

	@Override
	public HttpEntity getEntity() {
		return loader.getEntity();
	}
	
	@Override
	public void setEntity(final HttpEntity entity) {
		throw new UnsupportedOperationException();
	}
}