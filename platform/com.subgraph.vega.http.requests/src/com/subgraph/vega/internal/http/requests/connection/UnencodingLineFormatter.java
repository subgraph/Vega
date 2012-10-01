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
package com.subgraph.vega.internal.http.requests.connection;

import org.apache.http.RequestLine;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.util.CharArrayBuffer;

public class UnencodingLineFormatter extends BasicLineFormatter {
	@Override
	protected void doFormatRequestLine(final CharArrayBuffer buffer,
			final RequestLine reqline) {
		final String method = reqline.getMethod();
		final String uri = reqline.getUri();

		// room for "GET /index.html HTTP/1.1"
		int len = method.length() + 1 + uri.length() + 1
				+ estimateProtocolVersionLen(reqline.getProtocolVersion());
		buffer.ensureCapacity(len);

		buffer.append(method);
		buffer.append(' ');
		buffer.append(getUnencoded(uri));
		buffer.append(' ');
		appendProtocolVersion(buffer, reqline.getProtocolVersion());
	}

	private boolean filterUnencodedChar(char c) {
		return (c <= 0x19 || c >= 0x80 || "#%&=+;,!$?".indexOf(c) != -1);
	}

	private boolean isHexChar(char c) {
		return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F')
				|| (c >= 'a' && c <= 'f');
	}

	private int hexCharToInt(char c) {
		if (c >= '0' && c <= '9')
			return (c - '0');
		if (c >= 'A' && c <= 'F')
			return 10 + (c - 'A');
		if (c >= 'a' && c <= 'f')
			return 10 + (c - 'a');
		return -1;

	}

	private char decodeHexChars(char c1, char c2) {
		int n1 = hexCharToInt(c1);
		int n2 = hexCharToInt(c2);
		return (char) ((n1 << 4) | n2);
	}

	private boolean containsEncodedCharacters(String uri) {
		return uri.length() >= 3 && uri.indexOf('%') != -1;
	}

	private String getUnencoded(String uri) {
		if (!containsEncodedCharacters(uri))
			return uri;

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (uri.length() - 2); i++) {
			char c = uri.charAt(i);
			if (uri.charAt(i) == '%' && isHexChar(uri.charAt(i + 1))
					&& isHexChar(uri.charAt(i + 2))) {
				char c1 = uri.charAt(i + 1);
				char c2 = uri.charAt(i + 2);
				char unencoded = decodeHexChars(c1, c2);
				
				if (!filterUnencodedChar(unencoded)) {
					sb.append(unencoded);
					i += 2;
				} else {
					sb.append('%');
				}
			} else {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}

}
