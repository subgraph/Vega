package com.subgraph.vega.internal.http.requests.config.scanner;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.internal.http.requests.RequestCopyHeadersInterceptor;
import com.subgraph.vega.internal.http.requests.VegaResponseProcessCookies;
import com.subgraph.vega.internal.http.requests.config.IHttpClientConfigurer;

public class ScannerHttpClientConfigurer implements IHttpClientConfigurer {

	@Override
	public void configureHttpClient(DefaultHttpClient client) {
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		client.addRequestInterceptor(new RequestCopyHeadersInterceptor());
		client.removeResponseInterceptorByClass(ResponseProcessCookies.class);
		client.addResponseInterceptor(new VegaResponseProcessCookies());
	}

	@Override
	public HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, IHttpRequestEngineFactory.DEFAULT_USER_AGENT);
		return params;
	}

}
