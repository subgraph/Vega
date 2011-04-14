package com.subgraph.vega.ui.httpeditor.text.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class EncodedCharacterRule extends AbstractSyntaxRule {

	public EncodedCharacterRule(IToken token) {
		super(token);
	}

	@Override
	protected boolean evaluateRule(ICharacterScanner scanner) {
		if(!matchSingleChar(scanner, '%'))
			return false;
		if(!isHexDigit(scanner.read())) {
			scanner.unread();
			return false;
		}
		if(!isHexDigit(scanner.read())) {
			scanner.unread();
			scanner.unread();
			return false;
		}
		return true;
	}
}
