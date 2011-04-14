package com.subgraph.vega.ui.httpeditor.text.highlight;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class IntegerRule extends AbstractSyntaxRule {

	public IntegerRule(IToken token) {
		super(token);
	}
	
	@Override
	protected boolean evaluateRule(ICharacterScanner scanner) {
		if(!matchWhitespace(scanner))
			return false;
		matchSingleChar(scanner, '-');
		return matchDigits(scanner) && matchWhitespace(scanner);
		
	}
}
