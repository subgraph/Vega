package com.subgraph.vega.internal.http.requests.connection;

import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

public class BasicThreadSafeClientConnectionManager extends ThreadSafeClientConnManager {

	public BasicThreadSafeClientConnectionManager(SchemeRegistry sr) {
		super(sr);
	}
	
	@Override 
	protected ClientConnectionOperator createConnectionOperator(final SchemeRegistry sr) { 
		return new BasicClientConnectionOperator(sr);
	}
}
