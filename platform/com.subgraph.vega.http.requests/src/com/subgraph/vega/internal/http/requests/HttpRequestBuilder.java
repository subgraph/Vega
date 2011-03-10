package com.subgraph.vega.internal.http.requests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpRequestBuilder implements IHttpRequestBuilder {
	private String host = "";
	private int hostPort = 80;
	private String method = "";
	private String path = "";
	private String protocolVersion = "";
	private final ArrayList<HttpHeaderBuilder> headerList = new ArrayList<HttpHeaderBuilder>();

	public HttpRequestBuilder() {
	}
	
	@Override
	public void setFromRequest(IRequestLogRecord request) throws URISyntaxException {
		setFromRequest(request.getRequest());
	}

	@Override
	public void setFromRequest(HttpRequest request) throws URISyntaxException {
		RequestLine requestLine = request.getRequestLine();
		method = requestLine.getMethod();
		final URI requestUri = new URI(requestLine.getUri());
		host = requestUri.getHost();
		hostPort = requestUri.getPort();
		if (hostPort == -1) {
			hostPort = 80;
		}
		path = requestUri.getPath();
		protocolVersion = requestLine.getProtocolVersion().toString();
		headerList.clear();
		for (Header h: request.getAllHeaders()) {
			headerList.add(new HttpHeaderBuilder(h));
		}
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHostPort(int port) {
		this.hostPort = port;
	}

	@Override
	public int getHostPort() {
		return hostPort;
	}

	@Override
	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getRequestLine() {
		return method + " " + path + " " + protocolVersion;
	}

	@Override
	public HttpHeaderBuilder addHeader(String name, String value) {
		HttpHeaderBuilder header = new HttpHeaderBuilder(name, value);
		headerList.add(header);
		return header;
	}

	@Override
	public void removeHeader(IHttpHeaderBuilder header) {
		headerList.remove(header);
	}

	@Override
	public void swapHeader(int idx1, int idx2) {
		if (idx1 < headerList.size() && idx2 < headerList.size() && idx1 != idx2) {
			HttpHeaderBuilder tmp = headerList.set(idx1, headerList.get(idx2));
			headerList.set(idx2, tmp);
		}
	}

	@Override
	public int getHeaderIdxOf(IHttpHeaderBuilder header) {
		return headerList.indexOf(header);
	}

	@Override
	public int getHeaderCnt() {
		return headerList.size();
	}

	@Override
	public IHttpHeaderBuilder getHeader(int idx) {
		return headerList.get(idx);
	}

	@Override
	public IHttpHeaderBuilder[] getHeaders() {
		return headerList.toArray(new HttpHeaderBuilder[headerList.size()]);
	}

	@Override
	public HttpUriRequest buildRequest() throws URISyntaxException {
		final URI requestUri = new URI("http://" + host + ":" + Integer.toString(hostPort) + path);
		final HttpUriRequest uriRequest =  methodStringToUriRequest(method, requestUri);

		if (uriRequest == null) {
			return null;
		}

		//uriRequest.setParams(request.getParams());

		for (HttpHeaderBuilder h: headerList) {
			uriRequest.addHeader(h.buildHeader());
		}
		return uriRequest;
	}

	private HttpUriRequest methodStringToUriRequest(String methodString, URI uri) {
		final String m = methodString.toUpperCase();
		if(m.equals("GET"))
			return new HttpGet(uri);
		else if(m.equals("POST"))
			return new HttpPost(uri);
		else if(m.equals("HEAD"))
			return new HttpHead(uri);
		else if(m.equals("PUT"))
			return new HttpPut(uri);
		else if(m.equals("DELETE"))
			return new HttpDelete(uri);
		else if(m.equals("OPTIONS"))
			return new HttpOptions(uri);
		else if(m.equals("TRACE"))
			return new HttpTrace(uri);
		else 
			throw new IllegalArgumentException("Illegal HTTP method name "+ methodString);
	}

}
