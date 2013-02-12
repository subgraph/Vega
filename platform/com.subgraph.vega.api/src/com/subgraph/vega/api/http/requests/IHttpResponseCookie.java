package com.subgraph.vega.api.http.requests;

import org.apache.http.cookie.ClientCookie;

public interface IHttpResponseCookie extends ClientCookie {
	String getHeader();
}
