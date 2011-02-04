package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.internal.http.errors.HttpNotFoundErrorDetector;

public class EngineHttpResponse implements IHttpResponse {
	private final Logger logger = Logger.getLogger("request-engine");
	private final URI requestUri;
	private final HttpHost host;
	private final HttpRequest originalRequest;
	private final HttpResponse rawResponse;
	private final IHTMLParser htmlParser;
	private final HttpNotFoundErrorDetector notFoundDetector;
	
	private String cachedString;
	private boolean stringExtractFailed;
	private boolean htmlParseFailed;
	private IHTMLParseResult htmlParseResult;

	EngineHttpResponse(URI uri, HttpHost host, HttpRequest originalRequest, HttpResponse rawResponse, IHTMLParser htmlParser, HttpNotFoundErrorDetector notFoundDetector) {
		this.requestUri = uri;
		this.host = host;
		this.originalRequest = originalRequest;
		this.rawResponse = rawResponse;
		this.htmlParser = htmlParser;
		this.notFoundDetector = notFoundDetector;
	}

	@Override
	public HttpRequest getOriginalRequest() {
		return originalRequest;
	}

	@Override
	public HttpResponse getRawResponse() {
		return rawResponse;
	}

	@Override
	public String getBodyAsString() {
		synchronized (rawResponse) {
			
			if(cachedString != null)
				return cachedString;
			if(stringExtractFailed || rawResponse.getEntity() == null)
				return null;
			try {
				cachedString = EntityUtils.toString(rawResponse.getEntity());
				return cachedString;
			} catch (ParseException e) {
				logger.log(Level.WARNING, "Error parsing response headers: "+ e.getMessage(), e);
				stringExtractFailed = true;
				return null;
			} catch (IOException e) {
				logger.log(Level.WARNING, "IO error extracting response entity: "+ e.getMessage(), e);
				stringExtractFailed = true;
				return null;
			}
		}
	}

	@Override
	public IHTMLParseResult getParsedHTML() {
		synchronized(rawResponse) {
			if(htmlParseFailed)
				return null;
			if(htmlParseResult != null)
				return htmlParseResult;
			final String body = getBodyAsString();
			if(body == null) {
				htmlParseFailed = true;
				return null;
			}
			htmlParseResult = htmlParser.parseString(body, requestUri);
			if(htmlParseResult == null) 
				htmlParseFailed = true;
			return htmlParseResult;
		}
	}

	@Override
	public HttpHost getHost() {
		return host;
	}

	@Override
	public boolean isHttpNotFoundError() {
		return notFoundDetector.isNotFoundErrorResponse(originalRequest, this);
	}
}
