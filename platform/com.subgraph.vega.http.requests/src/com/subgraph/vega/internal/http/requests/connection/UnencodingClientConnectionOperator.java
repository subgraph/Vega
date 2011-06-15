package com.subgraph.vega.internal.http.requests.connection;

import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.SchemeRegistry;

public class UnencodingClientConnectionOperator extends SocksModeClientConnectionOperator {

	public UnencodingClientConnectionOperator(SchemeRegistry sr) {
		super(sr);
	}
	
	@Override 
	public OperatedClientConnection createConnection() {
		return new UnencodingClientConnection();
	}
}
