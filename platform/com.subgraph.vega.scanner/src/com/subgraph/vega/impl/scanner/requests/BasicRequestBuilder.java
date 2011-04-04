package com.subgraph.vega.impl.scanner.requests;


import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.model.web.IWebPath;

public class BasicRequestBuilder extends AbstractRequestBuilder {

	public BasicRequestBuilder(IWebPath path) {
		super(path);
	}

	@Override
	public HttpUriRequest createBasicRequest() {
		return createPathRequest();
	}

	@Override
	public HttpUriRequest createAlteredRequest(String value, boolean append) {
		final String path = createPathWithSuffix(getBasePath(), value);
		return createRequestFromPath(path);
	}

	private String createPathWithSuffix(String oldPath, String suffix) {
		if(oldPath.endsWith("/") && suffix.startsWith("/"))
			return oldPath + suffix.substring(1);
		else if(oldPath.endsWith("/") || suffix.startsWith("/"))
			return oldPath + suffix;
		else
			return oldPath + "/" + suffix;
	}

	@Override
	public HttpUriRequest createAlteredParameterNameRequest(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NameValuePair getFuzzableParameter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFuzzable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "GET "+ getBasePath();
	}
}
