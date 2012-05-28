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

public class HeaderPartitionRule implements IPredicateRule {
	private final static String SEPARATORS = "()<>@,;:\\\"/[]?={} \t";
	
	private final IToken token;
	
	public HeaderPartitionRule(IToken token) {
		this.token = token;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(!parseHeaderName(scanner))
			return Token.UNDEFINED;
		int c;
		while(true) {
			c = scanner.read();
			if(c == ICharacterScanner.EOF || c == '\r' || c == '\n') {
				scanner.unread();
				return token;
			}
		}		
	}

	private boolean parseHeaderName(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return false;
		int readCount = 1;
		int c;
		
		while((c = scanner.read()) != ICharacterScanner.EOF) {
			if(c == ':' && readCount > 1) {
				return true;
			} 
			
			if(!isLegalHeaderNameChar(c))
				break;
			readCount += 1;
		}
		unread(scanner, readCount);
		return false;
	}
	
	private void unread(ICharacterScanner scanner, int n) {
		for(int i = 0; i < n; i++)
			scanner.unread();
	}

	private boolean isLegalHeaderNameChar(int c) {
		return (c > 31 && SEPARATORS.indexOf(c) == -1);
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}
}
