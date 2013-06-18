/*******************************************************************************
 * Copyright (c) 2013 Subgraph.
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
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();

		if(htmlParseResult != null) {
			uris.addAll(extractUrlsFromDocument(htmlParseResult.getJsoupDocument(), response.getBodyAsString()));
		} 
		
		if (response.getRawResponse().containsHeader("Location")) {
			VegaURI v = locationExtractor(response, response.getRawResponse().getFirstHeader("Location").getValue());
			if (v != null) {
				uris.add(v);
			}
		}
		
		return uris;
	}
	
	List<VegaURI> findHtmlUrls(HttpEntity entity, URI basePath) throws IOException {
		final String htmlString = inputStreamToString(entity.getContent());
		final Document document = Jsoup.parse(htmlString, basePath.toString());
		return extractUrlsFromDocument(document, htmlString);
	}
	
	private List<VegaURI> extractUrlsFromDocument(Document document, String html) {
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		uris.addAll(extractURIs(document, "a[href]", "abs:href"));
		uris.addAll(extractURIs(document, "[src]", "abs:src"));
		uris.addAll(extractURIs(document, "link[href]", "abs:href"));
		uris.addAll(extractURIs(document, "meta",""));
	    uris.addAll(responseBodyUriScanFast(document, html));
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
			String link;
			if (e.tagName().equals("meta") && e.attr("http-equiv").toLowerCase().equals("refresh")) {
				String candidateLink = extractMetaRefresh(document, e);
				if (!candidateLink.startsWith("http")) {
					link = absUri(document.baseUri(), candidateLink);
				}
				else {
					link = candidateLink;
				}
				URI uri = createURI(link);
				if(uri != null && hasValidHttpScheme(uri)) {
					final HttpHost targetHost = URIUtils.extractHost(uri);
					if(validateHost(targetHost)) {
						final VegaURI vegaURI = new VegaURI(targetHost, uri.getPath(), uri.getQuery());
						uris.add(vegaURI);
					}
				}
			} else {
				link = e.attr(attribute);
				
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
	
	private String extractMetaRefresh(Document document, Element e) {
		final String content = e.attr("content");
		String clean = content.replaceAll("\\s", "");
		if (clean.toLowerCase().contains("url=")) {
			return clean.split("=")[1];
		}
		return "";
	}
	
	private String absUri(String baseUri, String path) {
		final String link;
		
		URI u = createURI(baseUri);
		
		if (path.startsWith("/")) {
			link = u.getScheme() + "://" + u.getHost() + path;
		} else {
			int i = 0;
			int lastIndex = 0;
			for (i = 0; i <= u.getPath().length()-1; i++) {
			  if (u.getPath().charAt(i) == '/') {
				  lastIndex = i;
			  }
			}
			link = u.getScheme() + "://" + u.getHost() + u.getPath().substring(0,  lastIndex) + "/" + path;
		} 
		return link;
	}
	
	
	private ArrayList<VegaURI> responseBodyUriScanFast(Document document, String s) {
		
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		int i = 0;
		
		String l = s.toLowerCase();
		
		while (i < s.length()) {
			
			if (l.startsWith("http://", i) || l.startsWith("https://", i)) {
				
				if (l.startsWith("http://", i))
				{
					if (i+7 >= s.length()) {
						return uris;
					}
				}
				else if (l.startsWith("https://", i)) {
					if (i+8 >= s.length()) {
						return uris;
					}
				}
				
				int start = i;
				int index = start;
				Boolean finished = false;
				String link;
				
				while (index < s.length() && !finished) {
					
					// TODO : Some of these terminating chars are valid in URIs, e.g. ')'
					
					if (Character.isWhitespace(s.charAt(index)) || (s.charAt(index) == '"') || (s.charAt(index) == '\'') || (s.charAt(index) == '>') || (s.charAt(index) == '<') || (s.charAt(index) == ')') ) {
						link = s.substring(start, index);
						URI uri = createURI(link);
						
						if(uri != null && hasValidHttpScheme(uri)) {
							final HttpHost targetHost = URIUtils.extractHost(uri);
							if(validateHost(targetHost)) {
								final VegaURI vegaURI = new VegaURI(targetHost, uri.getPath(), uri.getQuery());
								uris.add(vegaURI);
							}
						}
						i = index + 1;
						finished = true;
					} else {
						index++;
					}
				}
			}
			i++;
		}
		return uris;
	}
	
	private VegaURI locationExtractor(IHttpResponse response, String v) {
		final String link;
		
		if (!v.startsWith("http://") && !v.startsWith("https://")) {
			link = absUri(response.getRequestUri().toString(), v);
		} else
		{
			link = v;
		}
		URI uri = createURI(link);
		if(uri != null && hasValidHttpScheme(uri)) {
			final HttpHost targetHost = URIUtils.extractHost(uri);
			if(validateHost(targetHost)) {
				return new VegaURI(targetHost, uri.getPath(), uri.getQuery());
			}
		}
		return null;
	}

}

