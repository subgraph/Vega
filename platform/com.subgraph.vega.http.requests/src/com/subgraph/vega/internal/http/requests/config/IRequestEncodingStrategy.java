package com.subgraph.vega.internal.http.requests.config;

import org.apache.http.RequestLine;

public interface IRequestEncodingStrategy {
	RequestLine encodeRequestLine(RequestLine requestLine);
}
