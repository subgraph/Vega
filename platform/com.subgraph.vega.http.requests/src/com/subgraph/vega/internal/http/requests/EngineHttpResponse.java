package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public class EngineHttpResponse implements IHttpResponse {
	private final Logger logger = Logger.getLogger("request-engine");
	private final URI requestUri;
	private final HttpRequest originalRequest;
	private final HttpResponse rawResponse;
	private String cachedString;
	private boolean stringExtractFailed;
	private Document cachedDocument;
	private boolean htmlExtractFailed;
	
	EngineHttpResponse(URI uri, HttpRequest originalRequest, HttpResponse rawResponse) {
		this.requestUri = uri;
		this.originalRequest = originalRequest;
		this.rawResponse = rawResponse;
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
	public Document getHtml() {
		synchronized(rawResponse) {
			if(htmlExtractFailed)
				return null;
		
			if(cachedDocument != null)
				return cachedDocument;
		
			final String body = getBodyAsString();
			if(body == null) {
				htmlExtractFailed = true;
				return null;
			}
		
			cachedDocument = Jsoup.parse(body, requestUri.toString());
			if(cachedDocument == null) {
				htmlExtractFailed = true;
				return null;
			}
			return cachedDocument;
		}
	}

}
