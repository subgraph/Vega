/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.analysis.urls;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.util.VegaURI;

public class HtmlUrlExtractor {
	
	List<VegaURI> findHtmlUrls(IHttpResponse response) {
		final IHTMLParseResult htmlParseResult = response.getParsedHTML();
		
		if(htmlParseResult != null) {
			return extractUrlsFromDocument(htmlParseResult.getJsoupDocument());
		} else {
			return Collections.emptyList();
		}
	}
	
	List<VegaURI> findHtmlUrls(HttpEntity entity, URI basePath) throws IOException {
		final String htmlString = inputStreamToString(entity.getContent());
		final Document document = Jsoup.parse(htmlString, basePath.toString());
		return extractUrlsFromDocument(document);
	}
	
	private List<VegaURI> extractUrlsFromDocument(Document document) {
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		uris.addAll(extractURIs(document, "a[href]", "abs:href"));
		uris.addAll(extractURIs(document, "[src]", "abs:src"));
		uris.addAll(extractURIs(document, "link[href]", "abs:href"));
		return uris;
	}
	
	private String inputStreamToString(InputStream in) throws IOException {
		final Reader r = new InputStreamReader(in, "UTF-8");
		final StringWriter w = new StringWriter();
		final char[] buffer = new char[8192];
		while(true) {
			int n = r.read(buffer, 0, buffer.length);
			if(n <= 0)
				return w.toString();
			w.write(buffer, 0, n);
		}
	}
	private List<VegaURI> extractURIs(Document document, String query, String attribute) {
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		for(Element e: document.select(query)) {
			String link = e.attr(attribute);
			link = link.replace("\\", "%5C");
			URI uri = createURI(link);
			if(uri != null && hasValidHttpScheme(uri)) {
				final HttpHost targetHost = URIUtils.extractHost(uri);
				if(validateHost(targetHost)) {
					final VegaURI vegaURI = new VegaURI(targetHost, uri.getPath(), uri.getQuery());
					uris.add(vegaURI);
				}
			}
		}
		return uris;
	}
	
	private boolean validateHost(HttpHost host) {
		if(host.getHostName() == null || host.getHostName().isEmpty()) {
			return false;
		}
		try {
			new URI(host.getSchemeName(), null, host.getHostName(), host.getPort(), null, null, null);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}

	private boolean hasValidHttpScheme(URI uri) {
		final String scheme = uri.getScheme();
		return (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")));
	}
	private URI createURI(String link) {
		try {
			if(link.isEmpty())
				return null;
			return new URI(link);
		} catch (URISyntaxException ex) {
			return null;
		}
	}
}
