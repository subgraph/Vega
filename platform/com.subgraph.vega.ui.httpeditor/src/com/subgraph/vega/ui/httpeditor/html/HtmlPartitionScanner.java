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
package com.subgraph.vega.ui.httpeditor.html;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

public class HtmlPartitionScanner extends RuleBasedPartitionScanner {
	public final static String HTML_COMMENT = "html_comment";
	public final static String HTML_DOCTYPE = "html_doctype";
	public final static String HTML_START_TAG = "html_start_tag";
	public final static String HTML_END_TAG = "html_end_tag";
	public final static String[] HTML_TYPES = {
		HTML_COMMENT, HTML_DOCTYPE, HTML_START_TAG, HTML_END_TAG
	};
	
	public HtmlPartitionScanner() {
		final List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new MultiLineRule("<!--", "-->", new Token(HTML_COMMENT)));
		rules.add(new MultiLineRule("</", ">", new Token(HTML_END_TAG)));
		IToken startTagToken = new Token(HTML_START_TAG);
		rules.add(new StartTagRule(startTagToken));
		rules.add(new StartTagRule(startTagToken));
		rules.add(new MultiLineRule("<!DOCTYPE", ">", new Token(HTML_DOCTYPE)));
		final IPredicateRule[] rs = new IPredicateRule[rules.size()];
		rules.toArray(rs);
		setPredicateRules(rs);
	}

}
