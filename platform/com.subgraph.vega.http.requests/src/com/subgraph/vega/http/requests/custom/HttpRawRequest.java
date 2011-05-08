package com.subgraph.vega.http.requests.custom;

import java.net.URI;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpRequestBase;

import com.subgraph.vega.api.http.requests.IHttpRawRequest;

public class HttpRawRequest extends HttpRequestBase implements IHttpRawRequest {
	private String rawRequestLine;
	private String method;

    public HttpRawRequest(final String rawRequestLine, final String method) {
        super();
		this.rawRequestLine = rawRequestLine;
        this.method = method;
    }

    public HttpRawRequest(final String rawRequestLine, final String method, final URI uri) {
        super();
		this.rawRequestLine = rawRequestLine;
        this.method = method;
        setURI(uri);
    }

	@Override
	public void setRawRequestLine(final String rawRequestLine) {
		this.rawRequestLine = rawRequestLine;
	}

	@Override
	public String getRawRequestLine() {
		return rawRequestLine;
	}

	@Override
	public void setMethod(final String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public RequestLine getRequestLine() {
        ProtocolVersion ver = getProtocolVersion();
        URI uri = getURI();
        String uritext = null;
        if (uri != null) {
            uritext = uri.toASCIIString();
        }
        if (uritext == null || uritext.length() == 0) {
            uritext = "/";
        }
        return new RawRequestLine(rawRequestLine, method, uritext, ver);
	}

}
