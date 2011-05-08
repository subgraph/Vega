package com.subgraph.vega.ui.httpviewer.syntax.js;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class JavascriptPartitionScanner extends RuleBasedPartitionScanner {
	
	public final static String JS_DEFAULT_TYPE = "js_default";
	public final static String JS_MULTILINE_COMMENT = "js_multiline_comment";
	public final static String[] JS_PARTITION_TYPES = new String[] { JS_DEFAULT_TYPE, JS_MULTILINE_COMMENT };
	
	public JavascriptPartitionScanner() {
		final List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new EndOfLineRule("//", Token.UNDEFINED));
		rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\'));
		rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\'));
		rules.add(new MultiLineRule("/*", "*/", new Token(JS_MULTILINE_COMMENT)));
		final IPredicateRule[] rs = new IPredicateRule[rules.size()];
		rules.toArray(rs);
		setPredicateRules(rs);
	}

}
