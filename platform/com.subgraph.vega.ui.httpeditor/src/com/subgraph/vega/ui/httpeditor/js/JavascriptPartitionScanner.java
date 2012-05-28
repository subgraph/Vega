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
package com.subgraph.vega.ui.httpeditor.js;

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
		setDefaultReturnToken(new Token(JS_DEFAULT_TYPE));
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
