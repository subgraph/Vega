package com.subgraph.vega.impl.scanner.requests;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

public interface IRequestBuilder {
	HttpUriRequest createBasicRequest();
	HttpUriRequest createAlteredRequest(String value, boolean append);
	HttpUriRequest createAlteredParameterNameRequest(String name);
	boolean isFuzzable();
	NameValuePair getFuzzableParameter();
}
