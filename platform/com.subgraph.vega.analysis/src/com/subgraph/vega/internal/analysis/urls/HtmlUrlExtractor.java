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
import org.jsoup.parser.Parser;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.util.VegaURI;

public class HtmlUrlExtractor {

	List<VegaURI> findHtmlUrls(IHttpResponse response) {
		final IHTMLParseResult htmlParseResult = response.getParsedHTML();
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();

		if(htmlParseResult != null) {
			uris.addAll(extractUrlsFromDocument(response, htmlParseResult.getJsoupDocument(), response.getBodyAsString()));
		} 
		
		if (response.getRawResponse().containsHeader("Location")) {
			VegaURI v = locationExtractor(response, response.getRawResponse().getFirstHeader("Location").getValue());
			if (v != null) {
				uris.add(v);
			}
		}
		
		return uris;
	}
	
	List<VegaURI> findHtmlUrls(IHttpResponse response, HttpEntity entity, URI basePath) throws IOException {
		final String htmlString = inputStreamToString(entity.getContent());
		final Document document = Jsoup.parse(htmlString, basePath.toString());
		return extractUrlsFromDocument(response, document, htmlString);
	}
	
	private List<VegaURI> extractUrlsFromDocument(IHttpResponse response, Document document, String html) {
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		uris.addAll(extractURIs(response, document, "a[href]", "abs:href"));
		uris.addAll(extractURIs(response, document, "[src]", "abs:src"));
		uris.addAll(extractURIs(response, document, "link[href]", "abs:href"));
		uris.addAll(extractURIs(response, document, "meta",""));
	    uris.addAll(responseBodyUriScanFast(response, document, html));
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
	private List<VegaURI> extractURIs(IHttpResponse response, Document document, String query, String attribute) {
		final ArrayList<VegaURI> uris = new ArrayList<VegaURI>();
		for(Element e: document.select(query)) {
			String link;
			if (e.tagName().equals("meta") && e.attr("http-equiv").toLowerCase().equals("refresh")) {
				String candidateLink = extractMetaRefresh(document, e);
				if (!candidateLink.startsWith("http://") && (!candidateLink.startsWith("https://"))) {
					link = absUri(response, document.baseUri(), candidateLink);
				}
				else {
					link = candidateLink;
				}
				URI uri = createURI(link);
				if(uri != null && hasValidHttpScheme(uri)) {
					final HttpHost targetHost = URIUtils.extractHost(uri);
					if(validateHost(targetHost)) {
						final VegaURI vegaURI = new VegaURI(targetHost, uri.normalize().getPath(), uri.getQuery());
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
						final VegaURI vegaURI = new VegaURI(targetHost, uri.normalize().getPath(), uri.getQuery());
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
			return new URI(Parser.unescapeEntities(link, false));
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
	
	
	private String absUri(IHttpResponse response, String baseUri, String path) {
		final String link;
		String parentPath = baseUri;
		URI u = null;
		boolean finished = false;
		int index = 0;
		int lastIndex = -1;
		
		if (path.startsWith("/")) {
			return response.getRequestUri().getScheme() + "://" + response.getRequestUri().getHost() + path;
		}
		
		if (baseUri.startsWith("http://") || (baseUri.startsWith("https://"))) {
			try {
				u = new URI(baseUri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (u != null) {
				parentPath = u.getPath();
			} else return "";
		} 
		


		while ((index < parentPath.length()) && !finished) {
			if (parentPath.startsWith(".php", index) ||  parentPath.startsWith(".html",index) || parentPath.startsWith(".asp", index) || parentPath.startsWith(".jsp", index)) {
				finished = true;
			} else if ((parentPath.charAt(index) == '/') || (parentPath.charAt(index) == '\\')) {
				lastIndex = index;
			}
			index++;
		}	
				
		if (finished) {
			if (lastIndex >= 0) {
				link = response.getRequestUri().getScheme() + "://" + response.getRequestUri().getHost() + "/" + parentPath.substring(0, lastIndex) + "/" + path;
			} else
			{
				link = response.getRequestUri().getScheme() + "://" + response.getRequestUri().getHost() + "/" + parentPath + "/" + path;
			}
		}
		else if (lastIndex >= 0) {
  		  link = response.getRequestUri().getScheme() + "://" + response.getRequestUri().getHost() + parentPath.substring(0, lastIndex) + "/" + path;
		} else
		{
			link = response.getRequestUri().getScheme() + "://" + response.getRequestUri().getHost() + parentPath + "/" + path;
		}
		return link;
	}
	
	private ArrayList<VegaURI> responseBodyUriScanFast(IHttpResponse response, Document document, String s) {
		
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
								final VegaURI vegaURI = new VegaURI(targetHost, uri.normalize().getPath(), uri.getQuery());
								uris.add(vegaURI);
							}
						}
						i = index + 1;
						finished = true;
					} else {
						index++;
					}
				}
			} else if (l.startsWith(".php",i) || l.startsWith(".asp",i) || l.startsWith(".jsp",i) || l.startsWith(".html",i)) {
			
				// found possible path
								
				Boolean finished = false;
				int fileOffset = i; // point of "file" discovery
				int index = fileOffset;
				int startOffset = 0;    // "file" start point
				int endOffset = s.length();      // "file" end point
				
				// work backwards
				
				while (index >= 0 && !finished) {
				  if (Character.isWhitespace(s.charAt(index)) || (s.charAt(index) == '(') || (s.charAt(index) == '"') || (s.charAt(index) == '\'') || (s.charAt(index) == '>') || (s.charAt(index) == '<') || (s.charAt(index) == ')')) {
					  startOffset = index+1;
					  finished = true;
				  } else {
					  index--;
				  }
				}
				
				index = startOffset;
				finished = false;
				
				// work forwards
								
				while (index < s.length() && !finished) {
				  if (Character.isWhitespace(s.charAt(index)) || (s.charAt(index) == '"') || (s.charAt(index) == '\'') || (s.charAt(index) == '>') || (s.charAt(index) == '<') || (s.charAt(index) == ')')) {
					  endOffset = index;
					  finished = true;
				      String link = s.substring(startOffset, endOffset);

					  if (!link.startsWith("http://") && !link.startsWith("https://")) {
							link = absUri(response, document.baseUri(), link);
					  }
							
				      URI uri = createURI(link);
						
					  if(uri != null && hasValidHttpScheme(uri)) {
						final HttpHost targetHost = URIUtils.extractHost(uri);
						if(validateHost(targetHost)) {
							final VegaURI vegaURI = new VegaURI(targetHost, uri.normalize().getPath(), uri.getQuery());
							uris.add(vegaURI);
						}
					  }
					  i = index + 1;
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
			link = absUri(response, response.getRequestUri().toString(), v);
		} else
		{
			link = v;
		}
		URI uri = createURI(link);
		if(uri != null && hasValidHttpScheme(uri)) {
			final HttpHost targetHost = URIUtils.extractHost(uri);
			if(validateHost(targetHost)) {
				return new VegaURI(targetHost, uri.normalize().getPath(), uri.getQuery());
			}
		}
		return null;
	}

}

