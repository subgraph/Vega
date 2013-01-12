package com.subgraph.vega.internal.http.requests.client;

import org.apache.commons.logging.Log;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

public class VegaRequestDirector extends DefaultRequestDirector {

	public VegaRequestDirector(final Log log,
			final HttpRequestExecutor requestExec,
			final ClientConnectionManager conman,
			final ConnectionReuseStrategy reustrat,
			final ConnectionKeepAliveStrategy kastrat,
			final HttpRoutePlanner rouplan, final HttpProcessor httpProcessor,
			final HttpRequestRetryHandler retryHandler,
			final RedirectStrategy redirectStrategy,
			final AuthenticationStrategy targetAuthStrategy,
			final AuthenticationStrategy proxyAuthStrategy,
			final UserTokenHandler userTokenHandler, final HttpParams params) {
		super(log, requestExec, conman, reustrat, kastrat, rouplan,
				httpProcessor, retryHandler, redirectStrategy,
				targetAuthStrategy, proxyAuthStrategy, userTokenHandler, params);
	}

	// httpclient patched to change this method private --> protected
	@Override
	protected RequestWrapper wrapRequest(final HttpRequest request)
			throws ProtocolException {
		if (request instanceof HttpEntityEnclosingRequest) {
			return new VegaEntityEnclosingRequestWrapper(
					(HttpEntityEnclosingRequest) request);
		} else {
			return new VegaRequestWrapper(request);
		}
	}

}
