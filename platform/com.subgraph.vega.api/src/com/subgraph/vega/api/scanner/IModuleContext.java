package com.subgraph.vega.api.scanner;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IModelProperties;

public interface IModuleContext extends IModelProperties {
	void error(HttpUriRequest request, IHttpResponse response, String message);
	void debug(String msg);
	void publishAlert(String type, String key, String message, HttpRequest request, IHttpResponse response, Object ...properties);
	void publishAlert(String type, String message, HttpRequest request, IHttpResponse response, Object ...properties);
}
