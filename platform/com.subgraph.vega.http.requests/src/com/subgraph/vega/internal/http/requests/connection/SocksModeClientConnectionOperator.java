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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


import org.apache.http.HttpHost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class SocksModeClientConnectionOperator extends DefaultClientConnectionOperator {

	private final boolean isSocksMode;
	
	public SocksModeClientConnectionOperator(SchemeRegistry sr) {
		super(sr);
		isSocksMode = System.getProperty("socksEnabled") != null;
	}

	@Override
	public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
		if(!isSocksMode) {
			super.openConnection(conn, target, local, context, params);
			return;
		}
		
		
		final Scheme scheme = schemeRegistry.getScheme(target.getSchemeName());		
		final SchemeSocketFactory sf = scheme.getSchemeSocketFactory();
		
		final int port = scheme.resolvePort(target.getPort());
		Socket sock = sf.createSocket(params);
		conn.opening(sock, target);
		InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved(target.getHostName(), port);
		InetSocketAddress localAddress = null;
		if(local != null) {
			localAddress = new InetSocketAddress(local, 0);
		}
		try {
			Socket connsock = sf.connectSocket(sock, remoteAddress, localAddress, params);
			if(sock != connsock) {
				sock = connsock;
				conn.opening(sock, target);
			}
			prepareSocket(sock, context, params);
			conn.openCompleted(sf.isSecure(sock), params);
			return;
		} catch (ConnectException ex) {
			throw new HttpHostConnectException(target, ex);
		}
	}
}
