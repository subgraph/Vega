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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import com.subgraph.vega.ui.httpeditor.Colors;
import com.subgraph.vega.ui.httpeditor.ITextEntityRenderer;
import com.subgraph.vega.ui.httpeditor.js.formatter.JavascriptFormatter;

public class JavascriptTextEntityRenderer implements ITextEntityRenderer {
	public final static String JS_DEFAULT_TYPE = "js_default";
	public final static String JS_MULTILINE_COMMENT = "js_multiline_comment";
	public final static String[] JS_PARTITION_TYPES = new String[] { JS_DEFAULT_TYPE, JS_MULTILINE_COMMENT };
	
	private final JavascriptFormatter formatter = new JavascriptFormatter();
	
	@Override
	public List<String> getPartitionTypes() {
		return Arrays.asList(JS_PARTITION_TYPES);
	}

	@Override
	public boolean matchContentType(String contentType) {
		return contentType.contains("javascript") || contentType.contains("json");
	}

	@Override
	public void addPartitionScannerRules(List<IPredicateRule> rules) {
		rules.add(new EndOfLineRule("//", Token.UNDEFINED));
		rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\'));
		rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\'));
		rules.add(new MultiLineRule("/*", "*/", new Token(JS_MULTILINE_COMMENT)));
	}

	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}

	@Override
	public void configurePresentationReconciler(
		PresentationReconciler reconciler, Colors colors) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new JavascriptScanner(colors));
		reconciler.setDamager(dr, JS_DEFAULT_TYPE);
		reconciler.setRepairer(dr, JS_DEFAULT_TYPE);
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colors.get(Colors.MULTI_LINE_COMMENT))));
		reconciler.setDamager(dr, JavascriptPartitionScanner.JS_MULTILINE_COMMENT);
		reconciler.setRepairer(dr, JavascriptPartitionScanner.JS_MULTILINE_COMMENT);
	}

	@Override
	public String formatText(String input) {
		return formatter.format(input);
	}

	@Override
	public String getLineSplitChars() {
		return " ;";
	}

	@Override
	public String getDefaultPartitionType() {
		return JS_DEFAULT_TYPE;
	}
}
