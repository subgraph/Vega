package com.subgraph.vega.internal.http.requests.unencoding;

import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;

public class UnencodingClientConnection extends DefaultClientConnection {
	@Override
	 protected HttpMessageWriter createRequestWriter(
	            final SessionOutputBuffer buffer,
	            final HttpParams params) {
	        return new HttpRequestWriter(buffer, new UnencodingLineFormatter(), params);
	    }
}
