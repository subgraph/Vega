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
package com.subgraph.vega.internal.http.proxy;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.http.proxy.IHttpProxyTransactionManipulator;

public class ProxyTransactionManipulator implements IHttpProxyTransactionManipulator {
	/**
	 * Conditional request headers. 
	 */
	private final static String[] CONDITIONAL_REQUEST_HEADERS = {
		"If-Match",
		"If-Modified-Since",
		"If-None-Match",
		"If-Range",
		"If-Unmodified-Since"
	};
	
	private String userAgent;
	private boolean userAgentOverride;
	private boolean disableBrowserCache;
	private boolean disableProxyCache;

	public ProxyTransactionManipulator() {
	}

	@Override
	public synchronized void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public synchronized void setUserAgentOverride(boolean override) {
		this.userAgentOverride = override;
	}

	@Override
	public synchronized void setBrowserCacheDisable(boolean disable) {
		this.disableBrowserCache = disable;
	}

	@Override
	public synchronized void setProxyCacheDisable(boolean disable) {
		this.disableProxyCache = disable;
	}

	@Override
	public synchronized void process(HttpRequest request) {
		if (userAgent != null) {
			if (userAgentOverride != false) {
				request.setHeader(new BasicHeader("User-Agent", userAgent));
			} else {
				HttpProtocolParams.setUserAgent(request.getParams(), userAgent);
			}
		}

		if (disableBrowserCache) {
			for (String header: CONDITIONAL_REQUEST_HEADERS) { 
				request.removeHeaders(header);
			}
		}

		if (disableProxyCache) {
			// instruct HTTP/1.1 caches to do an end-to-end reload
			request.setHeader(new BasicHeader("Cache-Control", "no-cache"));

			// instruct HTTP/1.0 caches to (hopefully) do an end-to-end reload
			request.setHeader(new BasicHeader("Pragma", "no-cache"));
		}
	}

	@Override
	public synchronized void process(HttpResponse response) {
	}

}
