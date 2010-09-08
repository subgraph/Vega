package com.subgraph.vega.ui.httpeditor.scanners;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import com.subgraph.vega.ui.httpeditor.rules.HeaderPartitionRule;
import com.subgraph.vega.ui.httpeditor.rules.RequestPartitionRule;

public class HttpRequestPartitionScanner extends RuleBasedPartitionScanner {

	public static final String HTTP_HEADER = "http_header";
	public static final String HTTP_REQUEST = "http_request";
	public static final String HTTP_RESPONSE = "http_response";
	public static final String IMAGE_TAG = "image_tag";
	public static final String[] TYPES = { HTTP_HEADER, HTTP_REQUEST, HTTP_RESPONSE, IMAGE_TAG };
	
	public HttpRequestPartitionScanner() {
		IToken httpHeader = new Token(HTTP_HEADER);
		IToken request = new Token(HTTP_REQUEST);
		IToken response = new Token(HTTP_RESPONSE);
		IToken image = new Token(IMAGE_TAG);
		IPredicateRule[] rules = new IPredicateRule[4];
		rules[0] = new RequestPartitionRule(request, true);
		rules[1] = new RequestPartitionRule(response, false);
		rules[2] = new HeaderPartitionRule(httpHeader);
		rules[3] = new EndOfLineRule("[Image: ", image);
		setPredicateRules(rules);
	}
}
