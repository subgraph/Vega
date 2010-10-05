package com.subgraph.vega.api.http.requests;

public interface IHttpRequestEngineFactory {
	IHttpRequestEngineConfig createConfig();
	IHttpRequestEngine createRequestEngine(IHttpRequestEngineConfig config);
}
