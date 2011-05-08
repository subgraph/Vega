package com.subgraph.vega.internal.http.requests;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.params.HttpParams;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;

public class HttpMessageBuilder implements IHttpMessageBuilder {
	private HttpParams params;
	private ProtocolVersion protocolVersion;
	private final ArrayList<HttpHeaderBuilder> headerList = new ArrayList<HttpHeaderBuilder>();
	private HttpEntity entity;

	@Override
	public void clear() {
		params = null;
		protocolVersion = null;
		headerList.clear();
		entity = null;
	}

	@Override
	public void setParams(HttpParams params) {
		this.params = params;
	}

	@Override
	public HttpParams getParams() {
		return params;
	}

	@Override
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion; 
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void setHeaders(Header[] headers) {
		headerList.clear();
		for (Header h: headers) {
			headerList.add(new HttpHeaderBuilder(h));
		}
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
	public void clearHeaders() {
		headerList.clear();
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
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	@Override
	public HttpEntity getEntity() {
		return entity;
	}

}
