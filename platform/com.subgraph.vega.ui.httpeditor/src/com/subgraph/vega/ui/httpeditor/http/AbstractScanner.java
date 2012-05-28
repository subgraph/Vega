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
package com.subgraph.vega.ui.httpeditor.http;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.subgraph.vega.ui.httpeditor.Colors;

public abstract class AbstractScanner extends RuleBasedScanner {

	private final Colors colors;
	
	protected AbstractScanner(Colors colors) {
		this.colors = colors;
		final List<IRule> rules = new ArrayList<IRule>();
		initializeRules(rules);
		final IRule[] ruleArray = new IRule[rules.size()];
		rules.toArray(ruleArray);
		setRules(ruleArray);
	}
	
	protected IToken createToken(RGB rgb) {
		final Color c = colors.get(rgb);
		final TextAttribute attribute = new TextAttribute(c);
		return new Token(attribute);
	}
	
	abstract protected void initializeRules(List<IRule> rules);
}
