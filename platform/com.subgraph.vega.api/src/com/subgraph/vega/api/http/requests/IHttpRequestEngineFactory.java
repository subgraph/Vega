package com.subgraph.vega.api.http.requests;

import org.apache.http.client.HttpClient;

public interface IHttpRequestEngineFactory {
	IHttpRequestEngineConfig createConfig();
	HttpClient createBasicClient();
	IHttpRequestEngine createRequestEngine(IHttpRequestEngineConfig config);
	IHttpRequestEngine createRequestEngine(HttpClient client, IHttpRequestEngineConfig config);
}
