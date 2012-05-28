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

public class HeaderNameRule implements IRule {
	private final static String SEPARATORS = "()<>@,;:\\\"/[]?={} \t";

	private final IToken token;
	
	public HeaderNameRule(IToken token) {
		this.token = token;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return Token.UNDEFINED;
		int readCount = 1;
		int c;
		while((c = scanner.read()) != ICharacterScanner.EOF) {
			if(c == ':' && readCount > 1)
				return token;
			if(!isLegalHeaderNameChar(c))
				break;
			readCount += 1;
		}
		rewind(scanner, readCount);
		return Token.UNDEFINED;
	}

	private void rewind(ICharacterScanner scanner, int count) {
		for(int i = 0; i < count; i++) 
			scanner.unread();
	}
	
	private boolean isLegalHeaderNameChar(int c) {
		return (c > 31 && SEPARATORS.indexOf(c) == -1);
	}
}
