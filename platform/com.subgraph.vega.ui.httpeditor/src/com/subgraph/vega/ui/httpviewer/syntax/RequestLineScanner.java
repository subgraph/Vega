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
package com.subgraph.vega.ui.httpviewer.syntax;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;

import com.subgraph.vega.ui.httpviewer.Colors;
import com.subgraph.vega.ui.httpviewer.syntax.rules.EncodedCharacterRule;
import com.subgraph.vega.ui.httpviewer.syntax.rules.HttpVersionRule;
import com.subgraph.vega.ui.httpviewer.syntax.rules.RequestMethodRule;

public class RequestLineScanner extends AbstractScanner {

	public RequestLineScanner(Colors colors) {
		super(colors);
	}

	@Override
	protected void initializeRules(List<IRule> rules) {
		rules.add(new RequestMethodRule(createToken(Colors.REQUEST_VERB)));
		rules.add(new EncodedCharacterRule(createToken(Colors.ENCODED_CHAR)));
		rules.add(new HttpVersionRule(createToken(Colors.HTTP_VERSION)));
	}

}
