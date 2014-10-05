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
package com.subgraph.vega.internal.http.requests;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.internal.http.requests.client.VegaHttpClient;
import com.subgraph.vega.internal.http.requests.config.IHttpClientConfigurer;
import com.subgraph.vega.internal.http.requests.config.RequestEngineConfig;
import com.subgraph.vega.internal.http.requests.connection.SocksSupportingThreadSafeClientConnectionManager;

public class BasicHttpClientFactory {

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
	
	static HttpClient createHttpClient(IHttpRequestEngine.EngineConfigType type) {
		final IHttpClientConfigurer configurer = RequestEngineConfig.getHttpClientConfigurer(type);
		final HttpParams params = configurer.createHttpParams();
		final ClientConnectionManager ccm = createConnectionManager(params);
		final DefaultHttpClient client = new VegaHttpClient(ccm, params);
		configurer.configureHttpClient(client);
		return client;
	}

	
	private static ClientConnectionManager createConnectionManager(HttpParams params) {
		final SchemeRegistry sr = createSchemeRegistry();
		return new SocksSupportingThreadSafeClientConnectionManager(sr);
	}
	
	private static SchemeRegistry createSchemeRegistry() {
		final SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		try {
			SSLSocketFactory ssf = new SSLSocketFactoryPlus(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;
				}
			}, new AllowAllHostnameVerifierPlus());
			sr.register(new Scheme("https", 443, ssf));
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception creating SSLSocketFactory", e);
		}
		return sr;
	}
}
