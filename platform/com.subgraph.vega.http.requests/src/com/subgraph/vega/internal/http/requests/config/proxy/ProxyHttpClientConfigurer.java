package com.subgraph.vega.internal.http.requests.config.proxy;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.internal.http.requests.RequestCopyHeadersInterceptor;
import com.subgraph.vega.internal.http.requests.RequestExtractCookiesInterceptor;
import com.subgraph.vega.internal.http.requests.VegaResponseProcessCookies;
import com.subgraph.vega.internal.http.requests.config.IHttpClientConfigurer;



public class ProxyHttpClientConfigurer implements IHttpClientConfigurer {

	@Override
	public void configureHttpClient(DefaultHttpClient client) {
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		configureRequestInterceptors(client);
		configureResponseInterceptors(client);
	}
	
	private void configureRequestInterceptors(DefaultHttpClient client) {
		client.clearRequestInterceptors();
		client.addRequestInterceptor(new RequestCopyHeadersInterceptor());
		client.addRequestInterceptor(new RequestExtractCookiesInterceptor());
	}
	
	private void configureResponseInterceptors(DefaultHttpClient client) {
		client.clearResponseInterceptors();
		client.addResponseInterceptor(new VegaResponseProcessCookies());
	}

	@Override
	public HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		return params;
	}
}
