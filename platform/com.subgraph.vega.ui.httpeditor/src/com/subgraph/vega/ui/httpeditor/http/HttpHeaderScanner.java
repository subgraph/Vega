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

import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.SingleLineRule;

import com.subgraph.vega.ui.httpeditor.Colors;

public class HttpHeaderScanner extends AbstractScanner {

	public HttpHeaderScanner(Colors colors) {
		super(colors);
	}

	@Override
	protected void initializeRules(List<IRule> rules) {
		rules.add(new HeaderNameRule(createToken(Colors.HEADER_NAME)));
		rules.add(new SingleLineRule("\"", "\"", createToken(Colors.STRING), '\\'));
		rules.add(new SingleLineRule("'", "'", createToken(Colors.STRING), '\\'));
		rules.add(new DateRule(createToken(Colors.DATE)));
		rules.add(new IntegerRule(createToken(Colors.INTEGER)));
	}
}
