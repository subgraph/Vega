package com.subgraph.vega.ui.httpviewer.syntax.js;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class JavascriptWhitespaceDetector implements IWhitespaceDetector {
	@Override
	public boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}
}
