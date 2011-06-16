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
package com.subgraph.vega.ui.httpviewer.partitioning;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

abstract class AbstractStartLinePartitionRule implements IPredicateRule {
	
	protected final int FLAG_UNTIL_SPACE = 1;
	protected final int FLAG_UNTIL_EOL = 2;
	protected final int FLAG_UNTIL_EOF = 4;
	
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
		return parseText(FLAG_UNTIL_SPACE);
	}

	protected String parseText(int flags) {
		final StringBuilder sb = new StringBuilder();
		int c;
		boolean lastCharCR = false;
		
		while((c = nextChar()) != ICharacterScanner.EOF) {
			switch(c) {
			case '\n':
				if((flags & (FLAG_UNTIL_EOL | FLAG_UNTIL_SPACE)) != 0) {
					if(lastCharCR)
						unread();
					unread();
					return sb.toString();
				}
				break;
			case '\r':
				if((flags & FLAG_UNTIL_SPACE) != 0) {
					unread();
					return sb.toString();
				}
				lastCharCR = true;
				break;
			case ' ':
				if((flags & FLAG_UNTIL_SPACE) != 0) {
					unread();
					return sb.toString();
				}
				break;
			}
				
			sb.append((char) c);
		}
		if((flags & FLAG_UNTIL_EOF) == 0) {
			unread(sb.length());
			return "";
		}
		return sb.toString();
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
