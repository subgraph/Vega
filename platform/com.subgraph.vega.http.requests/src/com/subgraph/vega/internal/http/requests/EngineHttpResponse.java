package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;

public class EngineHttpResponse implements IHttpResponse {
	private final Logger logger = Logger.getLogger("request-engine");
	private final URI requestUri;
	private final HttpHost host;
	private final HttpRequest originalRequest;
	private /*final*/ HttpResponse rawResponse;
	private final long requestTime;
	private final IHTMLParser htmlParser;
	
	private String cachedString;
	private PageFingerprint cachedFingerprint;
	private boolean stringExtractFailed;
	private boolean htmlParseFailed;
	private IHTMLParseResult htmlParseResult;
	private boolean isMostlyAsciiTestDone;
	private boolean isMostlyAscii;

	EngineHttpResponse(URI uri, HttpHost host, HttpRequest originalRequest, HttpResponse rawResponse, long requestTime, IHTMLParser htmlParser) {
		this.requestUri = uri;
		this.host = host;
		this.originalRequest = originalRequest;
		this.rawResponse = rawResponse;
		this.requestTime = requestTime;
		this.htmlParser = htmlParser;
	}

	@Override
	public HttpRequest getOriginalRequest() {
		return originalRequest;
	}

	@Override
	public synchronized void setRawResponse(HttpResponse response) {
		rawResponse = response;
	}

	@Override
	public synchronized HttpResponse getRawResponse() {
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
				logger.log(Level.WARNING, "IO error extracting response entity for request "+ originalRequest.getRequestLine().getUri() +" : "+ e.getMessage(), e);
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
	public HTMLDocument getDocument() {
		final IHTMLParseResult htmlResult = getParsedHTML();
		if(htmlResult == null) {
			return null;
		} else {
			return htmlParseResult.getDOMDocument();
		}
	}

	@Override
	public HttpHost getHost() {
		return host;
	}

	@Override
	public int getResponseCode() {
		return rawResponse.getStatusLine().getStatusCode();
	}

	@Override
	public boolean isFetchFail() {
		final int code = getResponseCode();
		return code == 503 || code == 504;
	}

	@Override
	public boolean isMostlyAscii() {
		if(isMostlyAsciiTestDone)
			return isMostlyAscii;
		
		final String body = getBodyAsString();
		if(body == null || body.isEmpty()) {
			isMostlyAscii = false;
			isMostlyAsciiTestDone = true;
			return false;
		}
		
		int total = (body.length() > 200) ? (200) : (body.length());
		int printable = 0;
		
		for(int i = 0; i < total; i++) {
			char c = body.charAt(i);
			if((c >= 0x20 && c <= 0x7F) || Character.isWhitespace(c))
				printable += 1;
		}
		isMostlyAscii = ((printable * 100) / total) > 90;
		isMostlyAsciiTestDone = true;
		return isMostlyAscii;
	}

	@Override
	public IPageFingerprint getPageFingerprint() {
		if(cachedFingerprint == null)
			cachedFingerprint = PageFingerprint.generateFromCodeAndString(getResponseCode(), getBodyAsString());
		return cachedFingerprint;
	}

	@Override
	public ResponseStatus getResponseStatus() {
		// XXX
		return ResponseStatus.RESPONSE_OK;
	}

	@Override
	public long getRequestMilliseconds() {
		return requestTime;
	}

	@Override
	public void lockResponseEntity() {
		final HttpEntity entity = rawResponse.getEntity();
		if(entity == null)
			return;
		try {
			final HttpEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			rawResponse.setEntity(newEntity);
			entity.consumeContent();
		} catch (IOException e) {
			logger.log(Level.WARNING, "I/O error while loading HTTP entity", e);
		}		
	}
}
