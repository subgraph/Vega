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
package com.subgraph.vega.internal.analysis;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public class CSSDetector {
	
	boolean isBodyCSS(IHttpResponse response) {
		if(!response.isMostlyAscii())
			return false;
		final String body = response.getBodyAsString();
		if(body == null || body.isEmpty())
			return false;
		
		int i = 0;
		char lastChar = 0;
		boolean first = false;
		
		while(i < body.length()) {
			i = skipWhitespace(body, i);
			i = skipComments(body, i);
			if(i >= body.length()) break;
			if(isCSSKeyword(body, i))
				return true;
			
			char c = body.charAt(i);
			if(c == '{') {
				if(!first || lastChar == 0 || (!Character.isLetterOrDigit(lastChar) && ("-_]*".indexOf(lastChar) == -1)))
					return false;
				else
					return true;
			}
			
			if(first) {
				if(!Character.isLetterOrDigit(c) && (":,.#_-*[]~=\"'>".indexOf(c) == -1))
					return false;				
			} else {
				if(!Character.isLetterOrDigit(c) && (".#_-*".indexOf(c) == -1))
					return false;
				first = true;
			}
			lastChar = c;
			i += 1;
		}
		return false;
	}

	private int skipWhitespace(String body, int idx) {
		while(idx < body.length() && Character.isWhitespace(body.charAt(idx)))
			idx += 1;
		return idx;
	}
	
	private int skipComments(String body, int idx) {
		while(idx < body.length()) {
			if(body.startsWith("<!--", idx)) {
				idx += 4;
			} else if(body.startsWith("//")) {
				int end = body.indexOf("\r\n", 2);
				idx = (end == -1) ? (body.length()) : (idx + end + 2);
			} else if(body.startsWith("/*")) {
				int end = body.indexOf("*/");
				idx = (end == -1) ? (body.length()) : (idx + end + 2);
			} else {
				return idx;
			}
		}
		return idx;
	}
	
	private boolean isCSSKeyword(String body, int idx) {
		if((idx + 1) < body.length() && body.charAt(idx) == '@') {
			int end = (idx + 8 > body.length()) ? (body.length()) : (idx + 8);
			String chunk = body.substring(idx + 1, end).toLowerCase();
			return (chunk.startsWith("import") || chunk.startsWith("media") || chunk.startsWith("charset"));
		}
		return false;
	}
}
