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
package com.subgraph.vega.http.requests.custom;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import com.subgraph.vega.api.http.requests.IHttpMutableRequest;

public class HttpEntityEnclosingMutableRequest extends HttpEntityEnclosingRequestBase implements IHttpMutableRequest {
	private String method;

    public HttpEntityEnclosingMutableRequest(final String method) {
        super();
        this.method = method;
    }

    public HttpEntityEnclosingMutableRequest(final String method, final URI uri) {
        super();
        this.method = method;
        setURI(uri);
    }

    @Override
    public void setMethod(final String method) {
    	this.method = method;
    }
    
	@Override
	public String getMethod() {
		return method;
	}
	
}
