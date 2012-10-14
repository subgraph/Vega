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
package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

/**
 * HTTP request parser based on HttpRequestParser and AbstractMessageParser. Attaches scheme and host information to a 
 * request line URI when the connection is performing SSL MITM so the request still contains an absoluteURI as it would
 * for an ordinary proxy request.
 */
public class VegaHttpRequestParser implements HttpMessageParser<HttpRequest> {
	private final VegaHttpServerConnection conn;
    private final SessionInputBuffer sessionBuffer;
    private final HttpRequestFactory requestFactory;
    private final CharArrayBuffer lineBuf;
    private final LineParser lineParser;
    private final int maxHeaderCount;
    private final int maxLineLen;

    public VegaHttpRequestParser(final VegaHttpServerConnection conn, final SessionInputBuffer buffer, final LineParser parser, final HttpRequestFactory requestFactory, final HttpParams params) {
        if (requestFactory == null) {
            throw new IllegalArgumentException("Request factory may not be null");
        }
        this.conn = conn;
        this.sessionBuffer = buffer;
        this.lineParser = parser;
        this.requestFactory = requestFactory;
        this.lineBuf = new CharArrayBuffer(128);
        this.maxHeaderCount = params.getIntParameter(CoreConnectionPNames.MAX_HEADER_COUNT, -1);
        this.maxLineLen = params.getIntParameter(CoreConnectionPNames.MAX_LINE_LENGTH, -1);
    }

    private RequestLine parseRequestLine(final SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
        lineBuf.clear();
        int i = sessionBuffer.readLine(lineBuf);
        if (i == -1) {
            throw new ConnectionClosedException("Client closed connection");
        }
        ParserCursor cursor = new ParserCursor(0, lineBuf.length());
        return lineParser.parseRequestLine(lineBuf, cursor);
    }
    
    @Override
    public HttpRequest parse() throws IOException, HttpException {
    	RequestLine requestLine;
        try {
        	requestLine = parseRequestLine(sessionBuffer);
        } catch (ParseException px) {
            throw new ProtocolException(px.getMessage(), px);
        }

        List<CharArrayBuffer> headerLines = new ArrayList<CharArrayBuffer>();
        Header[] headers = AbstractMessageParser.parseHeaders(sessionBuffer, maxHeaderCount, maxLineLen, lineParser, headerLines);

        if (conn.isSslConnection()) {
        	URI uri;
        	try {
        		uri = new URI(requestLine.getUri());
        	} catch (URISyntaxException e) {
        		throw new ProtocolException("Invalid URI: " + requestLine.getUri(), e);
        	}
            if (uri.getScheme() == null) {
            	final Header hostHeader = getFirstHeader(headers, HTTP.TARGET_HOST);
            	final StringBuilder buf = new StringBuilder();
            	if (hostHeader != null) {
            		// REVISIT: does using the Host header value instead of the SSL host risk opening another connection?
            		buf.append("https://");
            		buf.append(hostHeader.getValue());
            	} else {
            		buf.append(conn.getSslHost().toURI());
            	}
            	buf.append(uri.getRawPath());
            	if (uri.getRawQuery() != null) {
            		buf.append("?");
            		buf.append(uri.getRawQuery());
            	}

            	requestLine = new BasicRequestLine(requestLine.getMethod(), buf.toString(), requestLine.getProtocolVersion());
            }
        }

        HttpRequest message = requestFactory.newHttpRequest(requestLine);
        message.setHeaders(headers);
        return message;
    }

    private Header getFirstHeader(Header[] headers, String name) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].getName().equalsIgnoreCase(name)) {
                return headers[i];
            }
        }
        return null;
    }
    
}
