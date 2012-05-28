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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HttpVersionRule implements IRule {
	private final static char[] VERSION_PREFIX = "HTTP/".toCharArray();
	private final IToken token;
	
	public HttpVersionRule(IToken token) {
		this.token = token;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(!matchPrefix(VERSION_PREFIX, scanner))
			return Token.UNDEFINED;
		int readCount = VERSION_PREFIX.length;
		
		if(!readDigit(scanner)) {
			rewind(scanner, readCount);
			return Token.UNDEFINED;
		}
		readCount++;
		
		if(!readDot(scanner)) {
			rewind(scanner, readCount);
			return Token.UNDEFINED;
		}
		readCount++;
		
		if(!readDigit(scanner)) {
			rewind(scanner, readCount);
			return Token.UNDEFINED;
		}
		
		return token;
	}
	
	private boolean matchPrefix(char[] prefix, ICharacterScanner scanner) {
		for(int i = 0; i < prefix.length; i++) {
			int c = scanner.read();
			if((char)c != prefix[i]) {
				rewind(scanner, i + 1);
				return false;
			}
		}
		return true;
	}
	
	private boolean readDigit(ICharacterScanner scanner) {
		final int c = scanner.read();
		if(c >= '0' && c <= '9')
			return true;
		scanner.unread();
		return false;
	}
	
	private boolean readDot(ICharacterScanner scanner) {
		final int c = scanner.read();
		if(c == '.')
			return true;
		scanner.unread();
		return false;
	}
	
	
	private void rewind(ICharacterScanner scanner, int n) {
		for(int i = 0; i < n; i++)
			scanner.unread();
	}
}
