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
package com.subgraph.vega.ui.httpeditor.js.formatter;

public class JavascriptFormatterConfig {
	enum BraceStyle { COLLAPSE, EXPAND, END_EXPAND };
	BraceStyle braceStyle = BraceStyle.COLLAPSE;
	boolean preserveNewlines;
	boolean keepArrayIndentation;
	int indentCount = 4;
	int indentLevel = 0; // initial indentation
	int maxPreserveNewline;
	boolean bracesOnOwnLine;
	char indentChar;

}
