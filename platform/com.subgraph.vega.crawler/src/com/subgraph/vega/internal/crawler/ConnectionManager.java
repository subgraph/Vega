package com.subgraph.vega.internal.crawler;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class ConnectionManager {
	
	private final HttpClient httpClient;
	ConnectionManager() {
		SchemeRegistry schemeRegistry = createSchemeRegistry();
		ClientConnectionManager connectionManager = createConnectionManager(schemeRegistry);
		httpClient = new DefaultHttpClient(connectionManager, new BasicHttpParams());
	}
	
	HttpClient getHttpClient() {
		return httpClient;
	}
	
	private SchemeRegistry createSchemeRegistry() {
		final SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		return registry;
	}
	
	private ClientConnectionManager createConnectionManager(SchemeRegistry registry) {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 6);
		return new ThreadSafeClientConnManager(params, registry);
	}

}
