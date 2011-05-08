package com.subgraph.vega.ui.httpeditor.parser;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;

/**
 * Parse a HTTP response created within an editor to generate a HttpResponse.
 */
public class HttpResponseParser extends ParserBase {
	final private IHttpResponseBuilder builder;

	public HttpResponseParser(final IHttpResponseBuilder builder) {
		this.builder = builder;
	}
	
	public HttpResponseParser(IHttpRequestEngine requestEngine) {
		builder = requestEngine.createResponseBuilder();
	}

	/**
	 * Parse a HTTP response in a string to build a HttpResponse.
	 * 
	 * @param content HTTP response string.
	 * @param params Response parameters, or null.

	 * @return HttpResponse, or null if the given HTTP response was empty. 
	 * @throws UnsupportedEncodingException
	 */
	public HttpResponse parseResponse(final String content, HttpParams params) throws UnsupportedEncodingException {
		final CharArrayBuffer buf = new CharArrayBuffer(0);
		buf.append(content);
		final ParserCursor bufCursor = new ParserCursor(0, buf.length()); 
		final LineParser parser = new BasicLineParser();

		if (parseStatusLine(parser, builder, buf, bufCursor) < 0) {
			return null;
		}
		parseHeaders(parser, builder, buf, bufCursor);
		if (!bufCursor.atEnd()) {
			StringEntity entity = new StringEntity(buf.substring(bufCursor.getPos(), bufCursor.getUpperBound()));
			builder.setEntity(entity);
		}

		builder.setParams(params);
		
		return builder.buildResponse();
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
