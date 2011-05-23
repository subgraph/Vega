package com.subgraph.vega.http.requests.custom;

import java.net.URI;

import org.apache.http.client.methods.HttpRequestBase;

import com.subgraph.vega.api.http.requests.IHttpMutableRequest;

public class HttpMutableRequest extends HttpRequestBase implements IHttpMutableRequest {
	private String method;

    public HttpMutableRequest(final String method) {
        super();
        this.method = method;
    }

    public HttpMutableRequest(final String method, final URI uri) {
        super();
        this.method = method;
        setURI(uri);
    }

	@Override
	public void setMethod(final String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return method;
	}

}
