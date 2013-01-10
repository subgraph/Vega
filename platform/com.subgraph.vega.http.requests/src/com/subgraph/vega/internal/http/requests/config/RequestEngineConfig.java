package com.subgraph.vega.internal.http.requests.config;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.internal.http.requests.config.proxy.ProxyHttpClientConfigurer;
import com.subgraph.vega.internal.http.requests.config.proxy.ProxyRequestEncodingStrategy;
import com.subgraph.vega.internal.http.requests.config.scanner.ScannerHttpClientConfigurer;
import com.subgraph.vega.internal.http.requests.config.scanner.ScannerRequestEncodingStrategy;

public class RequestEngineConfig {
	
	public static IRequestEncodingStrategy getRequestEncodingStrategy(IHttpRequestEngine.EngineConfigType type) {
		switch(type) {
		case CONFIG_SCANNER:
			return new ScannerRequestEncodingStrategy();
		case CONFIG_PROXY:
			return new ProxyRequestEncodingStrategy();
		}
		throw new IllegalStateException();
	}

	public static IHttpClientConfigurer getHttpClientConfigurer(IHttpRequestEngine.EngineConfigType type) {
		switch(type) {
		case CONFIG_SCANNER:
			return new ScannerHttpClientConfigurer();
		case CONFIG_PROXY:
			return new ProxyHttpClientConfigurer();
		}
		throw new IllegalStateException();
	}
}
