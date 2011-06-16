/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.http.requests.connection;

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
