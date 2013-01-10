package com.subgraph.vega.internal.http.requests.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import com.subgraph.vega.internal.http.requests.RequestTimingHttpExecutor;

public class VegaHttpClient extends DefaultHttpClient {
	private final Log log = LogFactory.getLog(getClass());

	public VegaHttpClient(ClientConnectionManager ccm, HttpParams params) {
		super(ccm, params);
	}

	@Override
	protected HttpRequestExecutor createRequestExecutor() {
		return new RequestTimingHttpExecutor();
	}

	@Override
	protected RequestDirector createClientRequestDirector(
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

		return new VegaRequestDirector(log, requestExec, conman, reustrat,
				kastrat, rouplan, httpProcessor, retryHandler,
				redirectStrategy, targetAuthStrategy, proxyAuthStrategy,
				userTokenHandler, params) {

		};
	}

}
