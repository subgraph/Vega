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
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

abstract class AbstractStartLinePartitionRule implements IPredicateRule {
	
	private final IToken token;
	
	private ICharacterScanner scanner;
		
	AbstractStartLinePartitionRule(IToken token) {
		this.token = token;
	}

	protected int nextChar() {
		return scanner.read();
	}
	
	protected void unread() {
		scanner.unread();
	}
	
	protected boolean isFirstColumn() {
		return scanner.getColumn() == 0;
	}
	protected void unread(int n) {
		for(int i = 0; i < n; i++) 
			scanner.unread();
	}
	
	protected boolean detectSingleSpace() {
		if(nextChar() == ' ')
			return true;
		unread();
		return false;
	}

	protected String parseWord() {
		return parseUntilSpace(true);
	}

	protected String parseUntilSpace(boolean allowEOF) {
		final StringBuilder sb = new StringBuilder();
		int c;
		while((c = nextChar()) != ICharacterScanner.EOF) {
			if(c == '\n' || c == '\r' || c == ' ') {
				unread();
				return sb.toString();
			} else {
				sb.append((char) c);
			}
		}
		
		if(allowEOF) {
			return sb.toString();
		} else {
			unread(sb.length());
			return "";
		}
	}
	
	protected String parseUntilEOL(boolean allowEOF) {
		final StringBuilder sb = new StringBuilder();
		int c;
		while((c = nextChar()) != ICharacterScanner.EOF) {
			if(c == '\n') {
				unread();
				return sb.toString();
			}
			else if(c == '\r') {
				if(nextChar() == '\n') {
					unread(2);
					return sb.toString();
				}
			} else {
				sb.append((char) c);
			}
		}
		if(allowEOF) {
			return sb.toString();
		} else {
			unread(sb.length());
			return "";
		}
	}
	
	/*
	 * Matches "\n" or "\r\n"
	 */
	protected boolean detectEOL() {
		final int c = nextChar();
		if(c == '\n')
			return true;
		if(c == '\r') {
			if(nextChar() == '\n')
				return true;
			else
				unread();
		}
		unread();
		return false;
	}
		
	protected abstract boolean doEvaluate();
		
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return Token.UNDEFINED;
		
		this.scanner = scanner;

		if(doEvaluate()) {
			return token;
		} else {
			return Token.UNDEFINED;
		}
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}
	
	protected boolean isHttpVersion(String s) {
		if(s == null || !s.toUpperCase().startsWith("HTTP/"))
			return false;
		
		return isVersionDigits(s.substring(5));
	}
	
	private boolean isVersionDigits(String s) {
		final String[] digits = s.split("\\.");
		if(digits.length != 2)
			return false;
		return isInteger(digits[0]) && isInteger(digits[1]);
	}
	
	protected boolean isInteger(String s) {
		if(s == null || s.isEmpty())
			return false;
		
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
