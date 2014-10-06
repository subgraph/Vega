/*******************************************************************************
 * Copyright (c) 2013 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner.urls;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.jsoup.nodes.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLDocument;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.util.UriTools;
import com.subgraph.vega.api.util.VegaURI;
import com.subgraph.vega.impl.scanner.forms.FormProcessor;

public class ResponseAnalyzer {

	private final IContentAnalyzer contentAnalyzer;
	private final UriParser uriParser;
	private final UriFilter uriFilter;
	private final FormProcessor formProcessor;
	private final SQLErrorMessageDetector sqlDetector;
	private final boolean isProxyScan;
	private final static Logger logger = Logger.getLogger("modules");
	

	public ResponseAnalyzer(IScannerConfig config,
			IContentAnalyzer contentAnalyzer, UriParser uriParser,
			UriFilter uriFilter, boolean isProxyScan) {
		this.contentAnalyzer = contentAnalyzer;
		this.uriParser = uriParser;
		this.uriFilter = uriFilter;
		this.isProxyScan = isProxyScan;
		this.formProcessor = new FormProcessor(config, uriFilter, uriParser);
		this.sqlDetector = new SQLErrorMessageDetector(this);
	}

	public IContentAnalyzer getContentAnalyzer() {
		return contentAnalyzer;
	}

	public void analyzePivot(IInjectionModuleContext ctx, HttpUriRequest req,
			IHttpResponse res) {

	}

	public void analyzePage(IInjectionModuleContext ctx, HttpUriRequest req,
			IHttpResponse res) {

		final IContentAnalyzerResult result = contentAnalyzer.processResponse(
				res, false, true);
		if (isProxyScan) {
			return;
		}
		for (VegaURI u : result.getDiscoveredURIs()) {
			if (uriFilter.filter(u))
				uriParser.processUri(u);
		}
		formProcessor.processForms(ctx, req, res);
	}

	public void analyzeContent(IInjectionModuleContext ctx, HttpUriRequest req,
			IHttpResponse res) {
		analyzeHtml(ctx, req, res);
		if (!filterInjectedPath(res.getRequestUri().getPath())) {
			contentAnalyzer.processResponse(res, false, false);
		}
		sqlDetector.detectErrorMessages(ctx, req, res);
	}

	private void analyzeHtml(IInjectionModuleContext ctx, HttpUriRequest req,
			IHttpResponse res) {
		IHTMLParseResult html = res.getParsedHTML();
		if (html == null)
			return;
		HTMLDocument document = html.getDOMDocument();
		NodeList elements = document.getElementsByTagName("*");
		for (int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);
			if (node instanceof Element) {
				analyzeHtmlElement(ctx, req, res, (Element) node);
			}
		}
	}

	private boolean filterInjectedPath(String path) {
		return path.contains("vega://") || path.contains("//vega.invalid")
				|| pathContainsXssTag(path);
	}

	private boolean pathContainsXssTag(String path) {
		final int idx = path.indexOf("vvv");
		if (idx == -1) {
			return false;
		}
		return extractXssTag(path, idx) != null;
	}

	private void analyzeHtmlElement(IInjectionModuleContext ctx,
			HttpUriRequest req, IHttpResponse res, Element elem) {
		boolean remoteScript = false;
		NamedNodeMap attributes = elem.getAttributes();
		final String tag = elem.getTagName().toLowerCase();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node item = attributes.item(i);
			if (item instanceof Attr) {
				String n = ((Attr) item).getName().toLowerCase();
				String v = ((Attr) item).getValue().toLowerCase();
				if (match(tag, "script") && match(n, "src"))
					remoteScript = true;
				if ((v != null)
						&& (match(n, "href", "src", "action", "codebase") || (match(
								n, "value") && !match(tag, "input")))) {
					if (v.startsWith("vega://"))
						alert(ctx, "vinfo-url-inject", "URL injection into <"
								+ tag + "> tag", req, res, null);

					if (v.startsWith("http://vega.invalid/")
							|| v.startsWith("//vega.invalid/")) {
						if (match(tag, "script", "link")) {
							ctx.addStringHighlight(((Attr) item).getValue());
							alert(ctx, "vinfo-xss-inject",
									"URL injection into actively fetched field in tag <"
											+ tag + "> (high risk)", req, res,
									null);
						} else if (match(tag, "a")) {
							ctx.addStringHighlight(((Attr) item).getValue());
							alert(ctx, "vinfo-url-inject",
									"URL injection into anchor tag (low risk)",
									req, res, null);
						} else {
							ctx.addStringHighlight(((Attr) item).getValue());
							alert(ctx, "vinfo-url-inject",
									"URL injection into tag <" + tag + ">",
									req, res, null);
						}
					}

				}

				if ((v != null) && (n.startsWith("on") || n.equals("style"))) {
					checkJavascriptXSS(ctx, req, res, v);
				}

				if (match(tag, "script", "frame", "iframe")
						&& match(n, "src")
						&& ((v != null) && (v.startsWith("javascript:") || v
								.startsWith("vbscript:")))) {
					checkJavascriptXSS(ctx, req, res, v);
				}

				if (n.contains("vvv"))
					possibleXssAlert(ctx, req, res, n, n.indexOf("vvv"),
							"vinfo-xss-inject",
							"Injected XSS tag into HTML attribute value");
			}
		}

		if (tag.startsWith("vvv"))
			possibleXssAlert(ctx, req, res, tag, 0, "vinfo-xss-inject",
					"Injected XSS tag into HTML tag name");

		if (tag.equals("style") || (tag.equals("script") && !remoteScript)) {
			String content = elem.getTextContent();
			if (content != null)
				checkJavascriptXSS(ctx, req, res, content);
		}
	}

	private void possibleXssAlert(IInjectionModuleContext ctx,
			HttpUriRequest req, IHttpResponse res, String text, int offset,
			String type, String message) {
		final int[] xids = extractXssTag(text, offset);
		final String xidstring = extractXssString(text, offset);
		if (xids == null)
			return;
		if (xidstring == null)
			return;
		final HttpUriRequest xssReq = ctx.getPathState().getXssRequest(xids[0],
				xids[1]);
		if (xssReq != null) {
			if (text.length() > 20)
				ctx.addStringHighlight(text);
			else
				ctx.addStringHighlight(xidstring);
			alert(ctx, type, message, xssReq, res, null);
		} else {

			if (text.length() > 20)
				ctx.addStringHighlight(text);
			else
				ctx.addStringHighlight(xidstring);

			String path = req.getURI().normalize().getPath();
			String storedXSSContext;

			if (offset == 0) {
				int tagOffset = res.getBodyAsString().indexOf(text);
				if ((tagOffset + 26) < res.getBodyAsString().length())
					storedXSSContext = res.getBodyAsString().substring(
							tagOffset + 16, tagOffset + 26);
				else
					storedXSSContext = res.getBodyAsString().substring(
							tagOffset, res.getBodyAsString().length() - 1);
			} else if ((offset + 26) < text.length())
				storedXSSContext = text.substring(offset + 16, offset + 26);
			else
				storedXSSContext = text.substring(offset + 16,
						text.length() - 1);

			int i = path.indexOf('?');

			if (i != -1) {
				path = path.substring(0, i);
			}

			alert(ctx, "vinfo-xss-stored", message + " (from previous scan)",
					req, res, "vinfo-xss-stored:" + path + ":"
							+ storedXSSContext);
		}
	}

	private boolean match(String s, String... options) {
		if (s == null)
			return false;
		for (int i = 0; i < options.length; i++)
			if (s.equals(options[i]))
				return true;
		return false;
	}

	private String extractXssString(String text, int offset) {
		// 3 9
		// vvv000000v000000
		if (text.length() < (offset + 16))
			return null;
		if (text.charAt(offset + 9) != 'v')
			return null;
		return text.substring(offset, offset + 16);
	}

	private int[] extractXssTag(String text, int offset) {
		// 3 9
		// vvv000000v000000
		if (text.length() < (offset + 16))
			return null;
		if (text.charAt(offset + 9) != 'v')
			return null;

		final int[] res = new int[2];
		res[0] = extractXssId(text, offset + 3);
		res[1] = extractXssId(text, offset + 10);
		if (res[0] == -1 || res[1] == -1)
			return null;
		else
			return res;

	}

	private int extractXssId(String text, int offset) {
		if (text.length() < (offset + 6))
			return -1;
		final String idStr = text.substring(offset, offset + 6);
		try {
			return Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private void checkJavascriptXSS(IInjectionModuleContext ctx,
			HttpUriRequest req, IHttpResponse res, String text) {
		if (text == null)
			return;
		int lastWordIdx = 0;
		int idx = 0;
		int possibleUrlIdx = 0;
		
		boolean inQuote = false;
		boolean prevSpace = true;
		boolean possibleUrl = false;
		
		while (idx < text.length()) {
			idx = maybeSkipJavascriptComment(text, idx);
			if (idx >= text.length())
				return;
			char c = text.charAt(idx);
			if (!inQuote && (c == '\'' || c == '"')) {
				inQuote = true;
				
				if (matchStartsWith(text, lastWordIdx, "innerHTML", "open", "asyncRequest",
						"url", "href", "write", "src", "location", "action")) {
					possibleUrl = true;
					possibleUrlIdx = idx + 1;
				}
				
				if (matchStartsWith(text, lastWordIdx, "innerHTML", "open",
						"url", "href", "write")
						&& matchStartsWith(text, idx + 1, "//vega.invalid/",
								"http://vega.invalid", "vega:")) {
					alert(ctx, "vinfo-url-inject",
							"Injected URL in JS/CSS code", req, res, null);
				}
			} else if (c == '\'' || c == '"') {
				if (possibleUrl) {
					addPossibleUrl(req.getURI(),text.substring(possibleUrlIdx, idx));
					possibleUrlIdx = 0;
					possibleUrl = false;
				}
				inQuote = false;
			} else if (!inQuote && text.startsWith("vvv", idx)) {
				possibleXssAlert(ctx, req, res, text, idx, "vinfo-xss-inject",
						"Injected syntax into JS/CSS code");
			} else if (Character.isWhitespace(c) || c == '.') {
				prevSpace = true;
			} else if (prevSpace && Character.isLetterOrDigit(c)) {
				lastWordIdx = idx;
				prevSpace = false;
			}
			idx += 1;
		}
	}

	private void addPossibleUrl(URI uri, String candidateLink) {
		// TODO Auto-generated method stub
		String link;

		if (isProxyScan) {
			return;
		}
		
		if (!candidateLink.startsWith("http")) {
			link = absUri(uri, candidateLink);
		}
		else {
			link = candidateLink;
		}
		URI u = createURI(link);
		if(u != null && hasValidHttpScheme(u)) {
			final HttpHost targetHost = URIUtils.extractHost(u);
			if(validateHost(targetHost)) {
				final VegaURI vegaURI = new VegaURI(targetHost, u.normalize().getPath(), u.getQuery());
					if (uriFilter.filter(vegaURI))
					uriParser.processUri(vegaURI);
			}
		}		
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
	
	private String absUri(URI u, String path) {
		final String link;
				
		if (path.startsWith("/")) {
			link = u.getScheme() + "://" + u.getHost() + path;
		} else {
			int i = 0;
			int lastIndex = 0;
			for (i = 0; i <= u.normalize().getPath().length()-1; i++) {
			  if (u.normalize().getPath().charAt(i) == '/') {
				  lastIndex = i;
			  }
			}
			link = u.getScheme() + "://" + u.getHost() + u.normalize().getPath().substring(0,  lastIndex) + "/" + path;
		} 
		return link;
	}
	
	private boolean matchStartsWith(String str, int offset, String... options) {
		for (int i = 0; i < options.length; i++) {
			if (str.startsWith(options[i], offset))
				return true;
		}
		return false;
	}

	private int maybeSkipJavascriptComment(String text, int idx) {
		final int max = text.length();
		final int savedIdx = idx;
		if (idx >= max)
			return idx;
		final char c = text.charAt(idx++);
		// Skip escaped characters here too
		if (c == '\\')
			return idx + 1;
		if (c != '/')
			return savedIdx;
		if (idx >= max)
			return idx;
		final char c1 = text.charAt(idx++);
		if (c1 == '/') {
			while (idx < max) {
				char cc = text.charAt(idx);
				if (cc == '\n' || c == '\r')
					return idx;
				idx += 1;
			}
			return max;
		} else if (c1 == '*') {
			int end = text.indexOf("*/", idx);
			if (end == -1)
				return max;
			else
				return end + 2;
		}
		return savedIdx;

	}

	public void alert(IInjectionModuleContext ctx, String type, String message,
			HttpUriRequest request, IHttpResponse response, String key) {

		if (key == null) {
			key = createAlertKey(ctx, type, request);
		}
		String resource = request.getURI().normalize().getPath();

		int i = resource.indexOf('?');

		if (i != -1) {
			resource = resource.substring(0, i);
		}

		ctx.publishAlert(type, key, message, request, response, "resource",
				resource);
	}

	private String createAlertKey(IInjectionModuleContext ctx, String type,
			HttpUriRequest request) {
		if (ctx.getPathState().isParametric()) {
			final String uri = UriTools.stripQueryFromUri(request.getURI())
					.toString();
			return type + ":" + uri + ":"
					+ ctx.getPathState().getFuzzableParameter().getName();
		} else {
			return type + ":" + request.getURI();
		}
	}

}