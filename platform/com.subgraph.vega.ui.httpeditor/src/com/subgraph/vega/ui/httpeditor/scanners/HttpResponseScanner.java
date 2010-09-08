package com.subgraph.vega.ui.httpeditor.scanners;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.subgraph.vega.ui.httpeditor.highlight.ColorManager;
import com.subgraph.vega.ui.httpeditor.highlight.IHttpColorConstants;

public class HttpResponseScanner extends RuleBasedScanner {
	IToken defaultToken;
	
	public HttpResponseScanner(ColorManager colorManager) {
		defaultToken = new Token(new TextAttribute(colorManager.getColor(IHttpColorConstants.HTTP_REQUEST_METHOD)));
		setDefaultReturnToken(defaultToken);
	}
}