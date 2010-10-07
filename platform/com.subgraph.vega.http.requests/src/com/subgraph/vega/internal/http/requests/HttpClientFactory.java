package com.subgraph.vega.internal.http.requests;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpClientFactory {

	static HttpClient createHttpClient() {
		final HttpParams params = createHttpParams();
		final ClientConnectionManager ccm = createConnectionManager(params);
		final HttpClient client = new DefaultHttpClient(ccm, params);
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		return client;
	}

	private static ClientConnectionManager createConnectionManager(HttpParams params) {
		final SchemeRegistry sr = createSchemeRegistry();
		return new ThreadSafeClientConnManager(params, sr);
	}

	private static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		ConnManagerParams.setMaxTotalConnections(params, 10);
		return params;
	}

	private static SchemeRegistry createSchemeRegistry() {
		final SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		return sr;
	}
}
