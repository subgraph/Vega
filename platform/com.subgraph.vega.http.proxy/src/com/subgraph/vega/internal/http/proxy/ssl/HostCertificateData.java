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
package com.subgraph.vega.internal.http.proxy.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A simple data class which associates a hostname with a temporary <code>PrivateKey</code> and
 * <code>X509Certificate</code> chain which has been generated (and optionally signed with a generated CA
 * certificate) to intercept an SSL connection to the given hostname.
 */
public class HostCertificateData {
	private final String hostname;
	private final PrivateKey privateKey;
	private final X509Certificate[] certificateChain;
	
	HostCertificateData(String hostname, PrivateKey privateKey, X509Certificate[] certificateChain) {
		this.hostname = hostname;
		this.privateKey = privateKey;
		this.certificateChain = certificateChain;
	}

	String getHostname() {
		return hostname;
	}

	PrivateKey getPrivateKey() {
		return privateKey;
	}

	X509Certificate[] getCertificateChain() {
		return certificateChain;
	}
}
