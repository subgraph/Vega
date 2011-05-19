package com.subgraph.vega.internal.http.requests.connection;

import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;

public class BasicClientConnectionOperator extends DefaultClientConnectionOperator {

	public BasicClientConnectionOperator(SchemeRegistry sr) {
		super(sr);
	}
	
	@Override 
	public OperatedClientConnection createConnection() {
		return new BasicClientConnection();
	}

}
