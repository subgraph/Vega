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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class IntegerRule extends AbstractSyntaxRule {

	public IntegerRule(IToken token) {
		super(token);
	}
	
	@Override
	protected boolean evaluateRule(ICharacterScanner scanner) {
		if(!matchWhitespace(scanner))
			return false;
		matchSingleChar(scanner, '-');
		return matchDigits(scanner) && matchWhitespace(scanner);
		
	}
}
