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

public class EncodedCharacterRule extends AbstractSyntaxRule {

	public EncodedCharacterRule(IToken token) {
		super(token);
	}

	@Override
	protected boolean evaluateRule(ICharacterScanner scanner) {
		if(!matchSingleChar(scanner, '%'))
			return false;
		if(!isHexDigit(scanner.read())) {
			scanner.unread();
			return false;
		}
		if(!isHexDigit(scanner.read())) {
			scanner.unread();
			scanner.unread();
			return false;
		}
		return true;
	}
}
