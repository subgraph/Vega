package com.subgraph.vega.ui.httpviewer.partitioning;

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
		
		final String reason = parseText(FLAG_UNTIL_EOL);
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
