package com.subgraph.vega.api.http.requests;

import org.apache.http.Header;

public interface IHttpHeaderBuilder {
	public void setFromHeader(Header header);
	public void setName(String name);
	public String getName();
	public void setValue(String value);
	public String getValue();
	public Header buildHeader();
}
