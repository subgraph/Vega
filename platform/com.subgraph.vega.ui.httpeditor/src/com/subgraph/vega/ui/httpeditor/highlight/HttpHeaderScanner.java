package com.subgraph.vega.ui.httpeditor.highlight;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;

public class HttpHeaderScanner extends RuleBasedScanner {
	
	
	public HttpHeaderScanner(ColorManager colorManager) {
		final IToken defaultToken = colorManager.createHighlightingToken(IHttpColorConstants.HTTP_DEFAULT);
		setDefaultReturnToken(defaultToken);
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(createDateRule(colorManager));
		rules.add(createMethodRule(colorManager));
		rules.add(createVersionRule(colorManager));
		rules.add(createStringRule(colorManager));
		rules.add(createEncodedCharacterRule(colorManager));
		rules.add(createNumberRule(colorManager));
		rules.add(createHeaderNameRule(colorManager));
		
		setRules(rules.toArray(new IRule[0]));
	}
	
	private IRule createMethodRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_REQUEST_METHOD);
		return new RequestMethodRule(token);
	}
	
	private IRule createVersionRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_VERSION);
		return new HttpVersionRule(token);
	}
	
	private IRule createHeaderNameRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_HEADER_NAME);
		return new HeaderNameRule(token);
	}
	
	private IRule createStringRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_STRING);
		return new SingleLineRule("\"", "\"", token);
	}
	
	private IRule createDateRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_DATE);
		return new DateRule(token);
	}
	
	private IRule createNumberRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.HTTP_INTEGER);
		return new IntegerRule(token);
	}
	
	private IRule createEncodedCharacterRule(ColorManager cm) {
		final IToken token = cm.createHighlightingToken(IHttpColorConstants.ENCODED_CHARACTER);
		return new EncodedCharacterRule(token);
	}
}