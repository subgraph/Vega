package com.subgraph.vega.ui.httpviewer.syntax.js;

import org.eclipse.jface.text.rules.IWordDetector;

public class JavascriptWordDetector implements IWordDetector {

	@Override
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}

	@Override
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}
}
