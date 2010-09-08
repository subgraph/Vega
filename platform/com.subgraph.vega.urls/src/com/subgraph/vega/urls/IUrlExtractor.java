package com.subgraph.vega.urls;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;

public interface IUrlExtractor {
	List<URI> findUrls(HttpEntity entity, URI basePath);
}
