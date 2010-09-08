package com.subgraph.vega.api.model.web;

import java.util.List;

import org.apache.http.NameValuePair;

public interface IWebGetTarget extends IWebEntity {
	String getMimeType();
	void setMimeType(String mimeType);
	String getQuery();
	IWebPath getPath();
	List<NameValuePair> getParameters();
}
