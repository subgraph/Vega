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

public class ProxySSLInitializationException extends Exception {

	private static final long serialVersionUID = 1L;
	
	ProxySSLInitializationException(String message) {
		super(message);
	}

	ProxySSLInitializationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
