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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;

import com.subgraph.vega.ui.httpeditor.Colors;
import com.subgraph.vega.ui.httpeditor.ITextEntityRenderer;

public class HtmlTextEntityRenderer implements ITextEntityRenderer {
	public final static String HTML_COMMENT = "html_comment";
	public final static String HTML_DOCTYPE = "html_doctype";
	public final static String HTML_START_TAG = "html_start_tag";
	public final static String HTML_END_TAG = "html_end_tag";
	
	public final static String[] HTML_TYPES = {
		HTML_COMMENT, HTML_DOCTYPE, HTML_START_TAG, HTML_END_TAG
	};

	@Override
	public List<String> getPartitionTypes() {
		return Arrays.asList(HTML_TYPES);
	}

	@Override
	public boolean matchContentType(String contentType) {
		return contentType.contains("html");
	}

	@Override
	public void addPartitionScannerRules(List<IPredicateRule> rules) {
		rules.add(new MultiLineRule("<!--", "-->", new Token(HTML_COMMENT)));
		rules.add(new MultiLineRule("</", ">", new Token(HTML_END_TAG)));
		final IToken startTagToken = new Token(HTML_START_TAG);
		rules.add(new StartTagRule(startTagToken));
		rules.add(new StartTagRule(startTagToken));
		rules.add(new MultiLineRule("<!DOCTYPE", ">", new Token(HTML_DOCTYPE)));
	}

	@Override
	public void configurePresentationReconciler(
		PresentationReconciler reconciler, Colors colors) {
		addDamagerRepairer(reconciler, new TagScanner(colors), HtmlPartitionScanner.HTML_START_TAG);
		addDamagerRepairer(reconciler, new TagScanner(colors), HtmlPartitionScanner.HTML_END_TAG);
		addDamagerRepairer(reconciler, colors.get(Colors.MULTI_LINE_COMMENT), HtmlPartitionScanner.HTML_COMMENT);
		addDamagerRepairer(reconciler, colors.get(Colors.MULTI_LINE_COMMENT), HtmlPartitionScanner.HTML_DOCTYPE);
		addDamagerRepairer(reconciler, colors.get(Colors.OTHER), IDocument.DEFAULT_CONTENT_TYPE);
	}
		
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}

	private void addDamagerRepairer(PresentationReconciler pr, Color color, String type) {
		final TextAttribute ta = new TextAttribute(color);
		addDamagerRepairer(pr, new SingleTokenScanner(ta), type);
	}
	
	private void addDamagerRepairer(PresentationReconciler pr, ITokenScanner scanner, String type) {
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		pr.setDamager(dr, type);
		pr.setRepairer(dr, type);
	}

	@Override
	public String formatText(String input) {
		return input;
	}

	@Override
	public String getLineSplitChars() {
		return ">,";
	}

	@Override
	public String getDefaultPartitionType() {
		return null;
	}
}
