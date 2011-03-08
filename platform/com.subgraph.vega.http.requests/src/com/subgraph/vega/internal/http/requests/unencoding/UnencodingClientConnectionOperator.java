package com.subgraph.vega.internal.http.requests.unencoding;

import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;

public class UnencodingClientConnectionOperator extends DefaultClientConnectionOperator {

	public UnencodingClientConnectionOperator(SchemeRegistry sr) {
		super(sr);
	}
	
	@Override 
	public OperatedClientConnection createConnection() {
		return new UnencodingClientConnection();
	}
}
