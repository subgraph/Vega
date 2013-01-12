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
package com.subgraph.vega.ui.httpeditor.parser;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;

/**
 * Parser to parse user-entered requests into an IHttpRequestBuilder.
 */
public class HttpRequestParser extends ParserBase {
	private final IHttpRequestBuilder builder;
	private final boolean parseInlineEntities;

	public HttpRequestParser(final IHttpRequestBuilder builder, boolean parseInlineEntities) {
		this.builder = builder;
		this.parseInlineEntities = parseInlineEntities;
	}

	public HttpRequestParser(final IHttpRequestBuilder builder) {
		this(builder, true);
	}
	
//	public HttpRequestParser(IHttpRequestEngine requestEngine) {
//		this(requestEngine.createRequestBuilder(), true);
//	}

	/**
	 * Parse a manually-entered HTTP request into the IHttpRequestBuilder.
	 * 
	 * @param content Manually-entered HTTP request.
	 */
	public void parseRequest(final String content) throws URISyntaxException, UnsupportedEncodingException {
		final CharArrayBuffer buf = new CharArrayBuffer(0);
		buf.append(content);
		final ParserCursor bufCursor = new ParserCursor(0, buf.length()); 
		final LineParser parser = new BasicLineParser();

		stripLeadingWhitspace(buf, bufCursor);
		if (parseRequestLine(parser, builder, buf, bufCursor) < 0) {
			return;
		}
		builder.clearHeaders();
		parseHeaders(parser, builder, buf, bufCursor);
		if (!bufCursor.atEnd() && parseInlineEntities) {
			StringEntity entity = new StringEntity(buf.substring(bufCursor.getPos(), bufCursor.getUpperBound()));
			builder.setEntity(entity);
		}
	}

	public IHttpRequestBuilder getRequestBuilder() {
		return builder;
	}

	/**
	 * Read and parse the request line.
	 * 
	 * @param parser HC LineParser.
	 * @param builder HTTP request builder.
	 * @param buf
	 * @param bufCursor
	 * @return
	 * @throws URISyntaxException
	 */
	private int parseRequestLine(final LineParser parser, final IHttpRequestBuilder builder, final CharArrayBuffer buf, final ParserCursor bufCursor) throws URISyntaxException {
		final CharArrayBuffer lnBuf = new CharArrayBuffer(0);
		if (readLine(buf, bufCursor, lnBuf) < 1) {
			// no data! 
			return -1;
		}
		final ParserCursor lnCursor = new ParserCursor(0, lnBuf.length());

		String method, uri;
		ProtocolVersion version;
		if ((method = nextWord(lnBuf, lnCursor)) != null) {
			if ((uri = nextWord(lnBuf, lnCursor)) != null) {
				try {
					version = parser.parseProtocolVersion(lnBuf, lnCursor);
				} catch (ParseException e) {
					// treat the unparseable version as HTTP/1.1
					version = new ProtocolVersion("HTTP", 1, 1); 
				};
			} else {
				uri = "";
				version = null;//new ProtocolVersion("HTTP", 0, 9);
			}
		} else {
			method = lnBuf.toString();
			uri = "";
			version = null;//new ProtocolVersion("HTTP", 0, 9);
		}

		builder.setMethod(method);
		builder.setPath(uri);
		builder.setProtocolVersion(version);
		
		return 0;
	}
	
}
