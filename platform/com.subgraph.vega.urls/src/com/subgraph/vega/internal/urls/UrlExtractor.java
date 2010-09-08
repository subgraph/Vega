package com.subgraph.vega.internal.urls;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;

import com.subgraph.vega.urls.IUrlExtractor;

public class UrlExtractor implements IUrlExtractor {
	private final HtmlUrlExtractor htmlExtractor = new HtmlUrlExtractor();

	@Override
	public List<URI> findUrls(HttpEntity entity, URI basePath) {
		if(entity.getContentType() != null && entity.getContentType().getValue().contains("html"))
			try {
				return htmlExtractor.findHtmlUrls(entity, basePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return Collections.emptyList();
			}
		return Collections.emptyList();
	}
}
