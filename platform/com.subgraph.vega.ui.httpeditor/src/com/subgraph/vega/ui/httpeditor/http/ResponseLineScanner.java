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

import com.subgraph.vega.ui.httpeditor.Colors;

public class ResponseLineScanner extends AbstractScanner {

	public ResponseLineScanner(Colors colors) {
		super(colors);
	}

	@Override
	protected void initializeRules(List<IRule> rules) {
		rules.add(new HttpVersionRule(createToken(Colors.HTTP_VERSION)));
		rules.add(new IntegerRule(createToken(Colors.INTEGER)));
	}

}
