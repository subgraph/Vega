package com.subgraph.vega.internal.http.requests;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;

public class HttpHeaderBuilder implements IHttpHeaderBuilder {
	private String name;
	private String value;
	
	public HttpHeaderBuilder(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public HttpHeaderBuilder(Header header) {
		name = header.getName();
		value = header.getValue();
	}

	@Override
	public void setFromHeader(Header header) {
		name = header.getName();
		value = header.getValue();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Header buildHeader() {
		return new BasicHeader(name, value);
	}

}
