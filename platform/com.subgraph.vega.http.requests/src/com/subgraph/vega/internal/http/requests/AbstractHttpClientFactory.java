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

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

public abstract class AbstractHttpClientFactory {

	protected static SchemeRegistry createSchemeRegistry() {
		final SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		try {
			SSLSocketFactory ssf = new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
					return true;
				}
			}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			sr.register(new Scheme("https", 443, ssf));
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception creating SSLSocketFactory", e);
		}
		return sr;
	}
}
