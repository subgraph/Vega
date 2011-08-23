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
package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.model.tags.ITag;

public class EngineHttpResponse implements IHttpResponse {
	private final Logger logger = Logger.getLogger("request-engine");
	private final URI requestUri;
	private final HttpHost host;
	private final HttpRequest originalRequest;
	private /*final*/ HttpResponse rawResponse;
	private final long requestTime;
	private final IHTMLParser htmlParser;
	private final List<ITag> tagList = new ArrayList<ITag>();

	private String cachedString;
	private PageFingerprint cachedFingerprint;
	private boolean htmlParseFailed;
	private IHTMLParseResult htmlParseResult;
	private boolean isMostlyAsciiTestDone;
	private boolean isMostlyAscii;
	private long requestId = -1;

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
			
			if(cachedString != null) {
				return cachedString;
			}
			
			if(rawResponse.getEntity() == null) {
				cachedString = "";
				return cachedString;
			}

			try {
				cachedString = EntityUtils.toString(rawResponse.getEntity());
			} catch (ParseException e) {
				logger.log(Level.WARNING, "Error parsing response headers: "+ e.getMessage(), e);
				cachedString = "";
			} catch (IOException e) {
				logger.log(Level.WARNING, "IO error extracting response entity for request "+ originalRequest.getRequestLine().getUri() +" : "+ e.getMessage(), e);
				cachedString = "";
			}
			return cachedString;
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
		if(isMostlyAsciiTestDone) {
			return isMostlyAscii;
		}
		
		final String body = getBodyAsString();

		if(body == null || body.isEmpty()) {
			isMostlyAscii = true;
			isMostlyAsciiTestDone = true;
			return true;
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
	public boolean lockResponseEntity() {
		final HttpEntity entity = rawResponse.getEntity();
		if(entity == null)
			return false;
		try {
			final ByteArrayEntity newEntity = new ByteArrayEntity(EntityUtils.toByteArray(entity));
			newEntity.setContentType(entity.getContentType());
			newEntity.setContentEncoding(entity.getContentEncoding());
			rawResponse.setEntity(newEntity);
			EntityUtils.consume(entity);
			return true;
		} catch (IOException e) {
			logger.warning("Error loading entity for "+ getRequestUri().toString() +" : "+ e.getMessage());
			return false;
		}		
	}

	@Override
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	@Override
	public long getRequestId() {
		return requestId;
	}

	@Override
	public URI getRequestUri() {
		return requestUri;
	}

	@Override
	public List<ITag> getTags() {
		return Collections.unmodifiableList(new ArrayList<ITag>(tagList));
	}

	@Override
	public void addTag(ITag tag) {
		tagList.add(tag);
	}

	@Override
	public void removeTag(ITag tag) {
		tagList.remove(tag);
	}

}
