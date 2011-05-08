package com.subgraph.vega.ui.httpviewer.html;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class HtmlWhitespaceDetector implements IWhitespaceDetector {

	@Override
	public boolean isWhitespace(char c) {
		return c == ' ' || c == '\t' || c == '\n' || c == '\r';
	}
}
