package com.subgraph.vega.urls;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public interface IUrlExtractor {
	List<URI> findUrls(IHttpResponse response);
	List<URI> findUrls(HttpEntity entity, URI basePath);
}
