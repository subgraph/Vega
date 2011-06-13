package com.subgraph.vega.api.http.requests;

import org.apache.http.client.HttpClient;

public interface IHttpRequestEngineFactory {
	IHttpRequestEngineConfig createConfig();
	HttpClient createBasicClient();
	HttpClient createUnencodingClient();
	IHttpRequestEngine createRequestEngine(HttpClient client, IHttpRequestEngineConfig config);
}
