package com.subgraph.vega.ui.httpeditor.parser;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

/**
 * Class to parse a request created by a user in an editor.
 */
public class HttpRequestParser extends ParserBase {
	final private IHttpRequestBuilder builder;

	public HttpRequestParser(IHttpRequestEngine requestEngine) {
		builder = requestEngine.createRequestBuilder();
	}

	/**
	 * Parse a manually-entered request to build a HttpUriRequest.
	 * 
	 * @param content
	 * @return HttpUriRequest, or null if the 
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	public HttpUriRequest parseRequest(final String content) throws URISyntaxException, UnsupportedEncodingException {
		final CharArrayBuffer buf = new CharArrayBuffer(0);
		buf.append(content);
		final ParserCursor bufCursor = new ParserCursor(0, buf.length()); 
		final LineParser parser = new BasicLineParser();

		if (parseRequestLine(parser, builder, buf, bufCursor) < 0) {
			return null;
		}
		parseRequestHeaders(parser, builder, buf, bufCursor);
		if (!bufCursor.atEnd()) {
			StringEntity entity = new StringEntity(buf.substring(bufCursor.getPos(), bufCursor.getUpperBound()));
			builder.setEntity(entity);
		}

		return builder.buildRequest();
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
					// treat the version as HTTP/1.1
					version = new ProtocolVersion("HTTP", 1, 1); 
				};
			} else {
				uri = "";
				version = new ProtocolVersion("HTTP", 0, 9);
			}
		} else {
			method = lnBuf.toString();
			uri = "";
			version = new ProtocolVersion("HTTP", 0, 9);
		}

		builder.setMethod(method);
		builder.setFromUri(new URI(uri));
		builder.setProtocolVersion(version);
		builder.setRawRequestLine(lnBuf.toString());
		
		return 0;
	}

	private void parseRequestHeaders(final LineParser parser, final IHttpRequestBuilder builder, final CharArrayBuffer buf, final ParserCursor bufCursor) {
		final CharArrayBuffer lnBuf = new CharArrayBuffer(0);
		while (true) {
			lnBuf.clear();
			int idxPos = bufCursor.getPos();
			if (readLineHeader(buf, bufCursor, lnBuf) > 0) {
				try {
					Header header = parser.parseHeader(lnBuf); // REVISIT don't want an extra step
					builder.addHeader(header.getName(), header.getValue());
				} catch (ParseException e) {
					// for now we'll move the cursor back so the line gets treated as the start of the body
					bufCursor.updatePos(idxPos);
					return;
				}
			}
		}
	}
	
}
