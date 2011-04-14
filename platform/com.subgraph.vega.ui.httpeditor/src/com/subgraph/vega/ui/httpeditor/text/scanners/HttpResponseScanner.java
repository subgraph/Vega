package com.subgraph.vega.ui.httpeditor.text.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.subgraph.vega.ui.httpeditor.text.highlight.ColorManager;
import com.subgraph.vega.ui.httpeditor.text.highlight.IHttpColorConstants;

public class HttpResponseScanner extends RuleBasedScanner {
	IToken defaultToken;
	
	public HttpResponseScanner(ColorManager colorManager) {
		defaultToken = new Token(new TextAttribute(colorManager.getColor(IHttpColorConstants.HTTP_REQUEST_METHOD)));
		setDefaultReturnToken(defaultToken);
	}
}