package com.subgraph.vega.internal.http.requests.config.proxy;

import org.apache.http.RequestLine;

import com.subgraph.vega.internal.http.requests.config.IRequestEncodingStrategy;

public class ProxyRequestEncodingStrategy implements IRequestEncodingStrategy {

	@Override
	public RequestLine encodeRequestLine(RequestLine requestLine) {
		return requestLine;
	}
}
