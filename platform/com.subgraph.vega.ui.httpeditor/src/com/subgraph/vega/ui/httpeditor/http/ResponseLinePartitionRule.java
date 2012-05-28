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

import org.eclipse.jface.text.rules.IToken;

public class ResponseLinePartitionRule extends AbstractStartLinePartitionRule {

	public ResponseLinePartitionRule(IToken token) {
		super(token);
	}

	@Override
	protected boolean doEvaluate() {
		if(!isFirstColumn()) {
			return false;
		}
		final String version = parseWord();
		int readLength = version.length();
		if(!isHttpVersion(version)) {
			unread(readLength);
			return false;
		}
		
		if(!detectSingleSpace()) {
			unread(readLength);
			return false;
		}
		readLength += 1;
		
		final String status = parseWord();
		readLength += status.length();
		if(!isInteger(status)) {
			unread(readLength);
			return false;
		}
		
		if(!detectSingleSpace()) {
			unread(readLength);
			return false;
		}
		readLength += 1;
		
		final String reason = parseUntilEOL(true);
		readLength += reason.length();
		if(!isResponseReason(reason)) {
			unread(readLength);
			return false;
		}
		
		if(!detectEOL()) {
			unread(readLength);
			return false;
		}
		return true;		
	}
	
	private boolean isResponseReason(String s) {
		return (s != null && !s.isEmpty());
	}

}
