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
