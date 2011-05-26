package com.subgraph.vega.internal.model.requests;

import org.apache.http.HttpEntity;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import com.db4o.ObjectContainer;

public class RequestLogEntityEnclosingRequest extends BasicHttpEntityEnclosingRequest {

	private final long entityId;

	private transient LazyEntityLoader loader;

	public RequestLogEntityEnclosingRequest(ObjectContainer database, RequestLine requestline, long entityId) {
		super(requestline);
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
