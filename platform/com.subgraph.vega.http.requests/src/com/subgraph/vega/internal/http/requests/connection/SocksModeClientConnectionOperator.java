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
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpHost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
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
		
		/* We need to use reflection to set the private host field to the target hostname, 
		 * otherwise SSLSocketImpl blows up due to, it seems, https://bugs.openjdk.java.net/browse/JDK-8022081.
		 */
		
		if (sf.isSecure(sock) == true) {
			Class<?> c = sock.getClass();
			try {
				Field f = c.getDeclaredField("host");
				f.setAccessible(true);
				f.set(sock, target.getHostName());
			} catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
						
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
