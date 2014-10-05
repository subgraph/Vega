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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.HttpHost;
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
	
	
	static class AllowAllHostnameVerifierPlus implements X509HostnameVerifier {
		
	    public  void verify(
	            final String host,
	            final SSLSocket ssl)
	             {
	        // Allow everything - so never blowup.
	             }

		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void verify(String host, X509Certificate cert)
				throws SSLException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void verify(String host, String[] cns, String[] subjectAlts)
				throws SSLException {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
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

		final AllowAllHostnameVerifierPlus hostNameVerifier = new AllowAllHostnameVerifierPlus();
		
		final SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		try {
			SSLSocketFactory ssf = new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;
				}
			}, hostNameVerifier);
				
			sr.register(new Scheme("https", 443, ssf));

			} catch (Exception e) {
				throw new RuntimeException("Unexpected exception creating SSLSocketFactory", e);	
		}
		// final Scheme scheme = schemeRegistry.getScheme(target.getSchemeName());
		
		final Scheme scheme = sr.getScheme(target.getSchemeName());		
		
		final SchemeSocketFactory sf = scheme.getSchemeSocketFactory();
		
		final int port = scheme.resolvePort(target.getPort());
		Socket sock = sf.createSocket(params);
		
		
		conn.opening(sock, target);
		InetSocketAddress remoteAddress = InetSocketAddress.createUnresolved(target.getHostName(), port);
		
		//remoteAddress = new InetSocketAddress(target.getHostName(), port);
		
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
			ex.printStackTrace();
		//	throw new HttpHostConnectException(target, ex);
		} catch (Exception e) {
	//		System.out.println("Host:" + conn.getRemoteAddress().getHostName() + " Port: "+conn.getRemotePort());
			e.printStackTrace();
		}
	}
}
