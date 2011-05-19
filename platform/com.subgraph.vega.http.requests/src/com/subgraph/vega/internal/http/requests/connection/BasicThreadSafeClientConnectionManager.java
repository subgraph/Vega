package com.subgraph.vega.internal.http.requests.connection;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

public class BasicThreadSafeClientConnectionManager extends ThreadSafeClientConnManager {

	public BasicThreadSafeClientConnectionManager(HttpParams params, SchemeRegistry sr) {
		super(params, sr);
	}
	
	@Override 
	protected ClientConnectionOperator createConnectionOperator(final SchemeRegistry sr) { 
		return new BasicClientConnectionOperator(sr);
	}

}
