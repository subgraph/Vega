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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class HtmlUrlExtractor {
	
	List<URI> findHtmlUrls(IHttpResponse response) {
		final IHTMLParseResult htmlParseResult = response.getParsedHTML();
		
		if(htmlParseResult != null) {
			return extractUrlsFromDocument(htmlParseResult.getJsoupDocument());
		} else {
			return Collections.emptyList();
		}
	}
	
	List<URI> findHtmlUrls(HttpEntity entity, URI basePath) throws IOException {
		final String htmlString = inputStreamToString(entity.getContent());
		final Document document = Jsoup.parse(htmlString, basePath.toString());
		return extractUrlsFromDocument(document);
	}
	
	private List<URI> extractUrlsFromDocument(Document document) {
		final ArrayList<URI> uris = new ArrayList<URI>();
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
	private List<URI> extractURIs(Document document, String query, String attribute) {
		final ArrayList<URI> uris = new ArrayList<URI>();
		for(Element e: document.select(query)) {
			String link = e.attr(attribute);
			link = link.replace("\\", "%5C");
			URI uri = createURI(link);
			if(uri != null)
				uris.add(uri);
		}
		return uris;
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
