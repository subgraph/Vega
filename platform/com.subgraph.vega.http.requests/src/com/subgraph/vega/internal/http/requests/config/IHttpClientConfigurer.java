package com.subgraph.vega.internal.http.requests.config;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public interface IHttpClientConfigurer {
	void configureHttpClient(DefaultHttpClient client);
	HttpParams createHttpParams();
}
