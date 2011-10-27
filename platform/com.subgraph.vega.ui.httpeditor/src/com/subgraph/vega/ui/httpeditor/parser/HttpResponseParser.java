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

import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;

/**
 * Parser to parse user-entered responsesinto an IHttpResponseBuilder.
 */
public class HttpResponseParser extends ParserBase {
	final private IHttpResponseBuilder builder;
	private final boolean parseInlineEntities;

	public HttpResponseParser(final IHttpResponseBuilder builder, boolean parseInlineEntities) {
		this.builder = builder;
		this.parseInlineEntities = parseInlineEntities;
	}

	/**
	 * Parse a HTTP response in a string.
	 * 
	 * @param content HTTP response string.
	 * @return HttpResponse, or null if the given HTTP response was empty. 
	 * @throws UnsupportedEncodingException
	 */
	public void parseResponse(final String content) throws UnsupportedEncodingException {
		final CharArrayBuffer buf = new CharArrayBuffer(0);
		buf.append(content);
		final ParserCursor bufCursor = new ParserCursor(0, buf.length()); 
		final LineParser parser = new BasicLineParser();

		if (parseStatusLine(parser, builder, buf, bufCursor) < 0) {
			return;
		}
		builder.clearHeaders();
		parseHeaders(parser, builder, buf, bufCursor);
		if (!bufCursor.atEnd() && parseInlineEntities) {
			StringEntity entity = new StringEntity(buf.substring(bufCursor.getPos(), bufCursor.getUpperBound()));
			builder.setEntity(entity);
		}
	}
	
	public IHttpResponseBuilder getResponseBuilder() {
		return builder;
	}

	/**
	 * Read and parse the response line.
	 * 
	 * @param parser HC LineParser.
	 * @param builder HTTP response builder.
	 * @param buf
	 * @param bufCursor
	 * @return
	 */
	private int parseStatusLine(final LineParser parser, final IHttpResponseBuilder builder, final CharArrayBuffer buf, final ParserCursor bufCursor) {
		final CharArrayBuffer lnBuf = new CharArrayBuffer(0);
		if (readLine(buf, bufCursor, lnBuf) < 1) {
			// no data!
			return -1;
		}
		final ParserCursor lnCursor = new ParserCursor(0, lnBuf.length());

	    final StatusLine statusLine = parser.parseStatusLine(lnBuf, lnCursor);
	    builder.setFromStatusLine(statusLine);
		
		return 0;
	}
	
}
