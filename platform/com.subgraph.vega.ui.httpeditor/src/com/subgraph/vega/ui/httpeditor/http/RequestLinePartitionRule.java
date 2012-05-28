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
package com.subgraph.vega.ui.httpeditor.http;

import org.eclipse.jface.text.rules.IToken;

public class RequestLinePartitionRule extends AbstractStartLinePartitionRule {

	public RequestLinePartitionRule(IToken token) {
		super(token);
	}

	@Override
	protected boolean doEvaluate() {
		if(!isFirstColumn()) {
			return false;
		}
		final String method = parseWord();
		int readLength = method.length();
		if(!isRequestMethod(method)) {
			unread(readLength);
			return false;
		}
		
		if(!detectSingleSpace()) {
			unread(readLength);
			return false;
		}
		readLength += 1;
		
		final String uri = parseWord();
		readLength += uri.length();
		if(!isRequestUri(uri)) {
			unread(readLength);
			return false;
		}
		
		if(!detectSingleSpace()) {
			unread(readLength);
			return false;
		}
		readLength += 1;
		
		final String version = parseUntilEOL(true);
		readLength += version.length();
		if(!isHttpVersion(version)) {
			unread(readLength);
			return false;
		}
		
		if(!detectEOL()) {
			unread(readLength);
			return false;
		}
		return true;
	}
	
	private boolean isRequestMethod(String s) {
		if(s == null || s.isEmpty())
			return false;
		for(int i = 0; i < s.length(); i++) {
			if(!Character.isLetter(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	private boolean isRequestUri(String s) {
		return (s != null && !s.isEmpty());
	}
}
