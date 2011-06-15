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

public class JavascriptDetector {
	private final static int MAX_JS_WHITE = 16;
	
	boolean isBodyJavascript(IHttpResponse response) {
		if(!response.isMostlyAscii())
			return false;
		final String body = response.getBodyAsString();
		if(body == null || body.isEmpty())
			return false;
		
		int i = 0;
		boolean first = false;
		int whiteCount = 0;
		while(i < body.length()) {
			i = skipComments(body, i);
			if(i >= body.length()) break;
			if(body.startsWith(" saved from url=", i))
				return false;
			char c = body.charAt(i);
			
			if("({[\"'".indexOf(c) != -1) 
				return true;
				
			if(first && ("=;".indexOf(c) != -1))
				return true;
			
			if(!(Character.isLetterOrDigit(c) || Character.isWhitespace(c) || c == '_' ||  c == '$' || c == '.'))
				return false;
			
			
			if(!Character.isWhitespace(c)) {
				whiteCount += 1;
				if(whiteCount > MAX_JS_WHITE)
					return false;
			}
				
			first = true;
			i+= 1;
		}
		return false;
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
}
