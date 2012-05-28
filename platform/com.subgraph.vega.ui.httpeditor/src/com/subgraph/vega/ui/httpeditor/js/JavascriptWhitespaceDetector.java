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

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class JavascriptWhitespaceDetector implements IWhitespaceDetector {
	@Override
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
