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

import java.util.Arrays;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;

public class DateRule extends AbstractSyntaxRule {
	private final String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
	private final String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sep", "Oct", "Nov", "Dec" };
	
	public DateRule(IToken token) {
		super(token);
	}

	@Override
	protected boolean evaluateRule(ICharacterScanner scanner) {
		// Date: Tue, 3 Oct 1974 02:16:00 GMT
		return 
		matchDay(scanner) &&
		matchSingleChar(scanner, ',') &&
		matchWhitespace(scanner)  && 
		matchDigits(scanner, 1, 2) &&
		matchWhitespace(scanner) &&
		matchMonth(scanner) &&
		matchWhitespace(scanner) &&
		matchDigits(scanner, 4)  && 
		matchWhitespace(scanner) &&
		matchDigits(scanner, 2) &&
		matchSingleChar(scanner, ':') &&
		matchDigits(scanner, 2) &&
		matchSingleChar(scanner, ':') &&
		matchDigits(scanner, 2) &&
		matchWhitespace(scanner) &&
		matchGMT(scanner);  
	}
	
	private boolean matchDay(ICharacterScanner scanner) {
		return matchStringFromList(scanner, days);
	}
	
	private boolean matchMonth(ICharacterScanner scanner) {
		return matchStringFromList(scanner, months);
	}
	
	private boolean matchStringFromList(ICharacterScanner scanner, String[] list) {
		final char[] buffer = readThreeLetters(scanner);
		if(buffer == null) 
			return false;
		
		for(String s : list) 
			if(Arrays.equals(s.toCharArray(), buffer))
				return true;
		return false;
	}
	
	private boolean matchGMT(ICharacterScanner scanner) {
		int c = scanner.read(); readCount++;
		if(c != 'G') return false;
		c = scanner.read(); readCount++;
		if(c != 'M') return false;
		c = scanner.read(); readCount++;
		return c == 'T';
	}
	
	private char[] readThreeLetters(ICharacterScanner scanner) {
		final char[] buffer = new char[3];
		
		int c = scanner.read(); readCount++;
		if(!isUppercase(c)) 
			return null;
		buffer[0] = (char) c;
		
		c = scanner.read(); readCount++;
		if(!isLowercase(c)) 
			return null;
		
		buffer[1] = (char) c;
		
		c = scanner.read(); readCount++;
		if(!isLowercase(c))
			return null;
		buffer[2] = (char) c;
		return buffer;
	}
}
