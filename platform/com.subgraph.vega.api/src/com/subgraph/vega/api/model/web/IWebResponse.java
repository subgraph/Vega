package com.subgraph.vega.api.model.web;

import java.util.List;

import org.apache.http.NameValuePair;

public interface IWebResponse extends IWebEntity {
	IWebPath getPathEntity();
	String getQueryString();
	List<NameValuePair> getRequestParameters();
	String getMimeType();
}
