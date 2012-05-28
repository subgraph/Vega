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

public class FormatterState {
	enum Mode { 
		ARRAY_EXPR (true, true), 
		ARRAY_INDENTED_EXPR (true, true), 
		PAREN_EXPR (false, true), 
		DO_BLOCK (false, false), 
		BLOCK (false, false), 
		OBJECT (false, false);
		final boolean isArray;
		final boolean isExpression;
		Mode(boolean array, boolean expression) {
			this.isArray = array;
			this.isExpression = expression;
		}
	};
	int indentationLevel;
	int indentationBaseline;
	boolean eatNextSpace;
	boolean ifLine;
	boolean varLine;
	boolean varLineTainted;
	boolean varLineReindented;
	boolean inCase;
	boolean inHtmlComment;
	int ternaryDepth;
	
	
	Mode mode;
	Mode previousMode;
	
	FormatterState(Mode mode) {
		this(null, mode, null);
	}

	FormatterState(JavascriptFormatterConfig config, Mode mode, FormatterState previousState) {
		this.mode = mode;
		varLine = false;
		varLineTainted = false;
		varLineReindented = false;
		ifLine = false;
		inCase = false;
		eatNextSpace = false;
		indentationBaseline = -1;
		ternaryDepth = 0;
		
		if(previousState != null) {
			previousMode = previousState.mode;
			indentationLevel = previousState.indentationLevel;
			if(previousState.varLine && previousState.varLineReindented)
				indentationLevel += 1;
		} else {
			previousMode = Mode.BLOCK;
			indentationLevel = (config == null) ? 0 : config.indentLevel;
		}
	}
}
