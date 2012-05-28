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

import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.subgraph.vega.ui.httpeditor.Colors;
import com.subgraph.vega.ui.httpeditor.http.AbstractScanner;

public class TagScanner extends AbstractScanner {
	private final static String[] HTML_TAGS = new String[] {
		"a", "abbr", "acronym", "address", "area", "b", "base", "bdo", "big",
		"blockquote", "body", "br", "button", "caption", "cite", "code", "col", "colgroup",
		"dd", "del", "dfn", "div", "dl", "dt", "em", "fieldset", "form", "frame", "frameset",
		"h1", "h2", "h3", "h4", "h5", "h6", "head", "tr", "html", "i", "iframe", "img", "input", "ins", "kbd",
		"label", "legend", "li", "link", "map","meta", "noframes", "noscript", "object", "ol", "optgroup",
		"option", "p", "param", "pre", "q", "samp", "script", "select", "small", "span", "strong", "style",
		"sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt",  
		"ul", "var"
	};
	static class TagWordDetector implements IWordDetector {
		@Override
		public boolean isWordStart(char c) {
			return Character.isLetter(c);
		}
		@Override
		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}		
	}

	public TagScanner(Colors colors) {
		super(colors);
		setDefaultReturnToken(createToken(Colors.TAG));
	}

	@Override
	protected void initializeRules(List<IRule> rules) {

		final IToken string = createToken(Colors.STRING);
		
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("\'", "\'", string, '\\'));
		rules.add(new WhitespaceRule(new HtmlWhitespaceDetector()));
		final WordRule words = new WordRule(new TagWordDetector(), createToken(Colors.OTHER), true);
		final IToken tag = createToken(Colors.KEYWORD);
		for(String s: HTML_TAGS) {
			words.addWord(s, tag);
		}
		rules.add(words);
	}	
}
