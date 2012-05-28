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

public abstract class AbstractSyntaxRule implements IRule {
	
	private final IToken token;
	protected int readCount;
	
	protected AbstractSyntaxRule(IToken token) {
		this.token = token;
	}
	
	protected IToken getSuccessToken() {
		return token;
	}
	
	protected abstract boolean evaluateRule(ICharacterScanner scanner);
	
	public IToken evaluate(ICharacterScanner scanner) {
		readCount = 0;
		if(evaluateRule(scanner))
			return token;
		
		rewind(scanner);
		return Token.UNDEFINED;
	}
	
	private void rewind(ICharacterScanner scanner) {
		for(int i = 0; i < readCount; i++)
			scanner.unread();
	}
	
	protected boolean matchSingleChar(ICharacterScanner scanner, char ch) {
		if(scanner.read() == ch) {
			readCount++;
			return true;
		} else {
			scanner.unread();
			return false;
		}
	}
	
	protected boolean matchDigits(ICharacterScanner scanner) {
		return matchAndCountDigits(scanner) > 0;
	}
	
	protected boolean matchDigits(ICharacterScanner scanner, int count) {
		return matchDigits(scanner, count, count);
	}
	
	protected boolean matchDigits(ICharacterScanner scanner, int min, int max) {
		final int count = matchAndCountDigits(scanner);
		return (count <= max && count >= min);
	}
	
	protected int matchAndCountDigits(ICharacterScanner scanner) {
		int digitCount = 0;
		while(true) {
			int c = scanner.read();
			if(isDigit(c)) {
				digitCount++;
				readCount++;
			} else {
				scanner.unread();
				return digitCount;
			}
		}
	}
	
	protected void skipWhitespace(ICharacterScanner scanner) {
		while(true) {
			int c = scanner.read();
			if(!isWhitespace(c)) {
				scanner.unread();
				return;
			}
			readCount++;
		}
	}
	
	/* match one or more whitespace characters */
	protected boolean matchWhitespace(ICharacterScanner scanner) {
		final int c = scanner.read(); readCount++;
		if(!isWhitespace(c)) 
			return false;
		
		while(true) {
			if(!isWhitespace(scanner.read())) {
				scanner.unread();
				return true;
			}
			readCount++;
		}
	}
	
	protected boolean isDigit(int c) {
		return c >= '0' && c <= '9';
	}
	
	protected boolean isWhitespace(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c =='\t';
	}
	
	protected boolean isUppercase(int c) {
		return c >= 'A' && c <= 'Z';
	}
	
	protected boolean isLowercase(int c) {
		return c >= 'a' && c <= 'z';
	}
	
	protected boolean isHexDigit(int c) {
		return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'); 
	}
}
