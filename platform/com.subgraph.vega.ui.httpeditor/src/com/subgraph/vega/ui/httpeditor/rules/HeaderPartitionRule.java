package com.subgraph.vega.ui.httpeditor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class HeaderPartitionRule implements IPredicateRule {

	private final IToken token;
	
	public HeaderPartitionRule(IToken token) {
		this.token = token;
	}
	
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0) {
			return Token.UNDEFINED;
		}
		boolean seenColon = false;
		while(true) {
			int c = scanner.read();
			if(c == ICharacterScanner.EOF)
				return Token.UNDEFINED;
			if(c == ':') seenColon = true;
			if(c == '\n') {
				if(seenColon) {
					return token;
				}
				else {
					return Token.UNDEFINED;
				}
			}
		}
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}
}