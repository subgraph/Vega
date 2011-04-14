package com.subgraph.vega.ui.httpeditor.text.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HeaderNameRule implements IRule {
	private final IToken token;
	public HeaderNameRule(IToken token) {
		this.token = token;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return Token.UNDEFINED;
		while(true) {
			int c = scanner.read();
			if(c == ICharacterScanner.EOF)
				return Token.UNDEFINED;
			if(c == ':') 
				return token;
		}
	
	}
}