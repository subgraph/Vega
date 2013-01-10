package com.subgraph.vega.http.requests.custom;

import com.subgraph.vega.internal.http.requests.config.IRequestEncodingStrategy;

public interface IEncodableHttpRequest {
	void encodeWith(IRequestEncodingStrategy encodingStrategy);
}
