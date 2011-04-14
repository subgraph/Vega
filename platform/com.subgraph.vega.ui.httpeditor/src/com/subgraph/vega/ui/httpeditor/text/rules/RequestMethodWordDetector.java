package com.subgraph.vega.ui.httpeditor.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class RequestMethodWordDetector implements IWordDetector {
	public static final String[] methods = {"GET", "PUT", "POST", "HEAD", "TRACE", "OPTIONS", "DELETE" };

	@Override
	public boolean isWordPart(char c) {
		return Character.isUpperCase(c);
	}

	@Override
	public boolean isWordStart(char c) {
		for(int i = 0; i < methods.length; i++)
			if(c == methods[i].charAt(0))
				return true;
		return false;
	}
}