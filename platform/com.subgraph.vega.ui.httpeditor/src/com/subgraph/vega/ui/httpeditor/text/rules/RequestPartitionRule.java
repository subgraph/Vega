package com.subgraph.vega.ui.httpeditor.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class RequestPartitionRule implements IPredicateRule {
	private final IToken token;
	private final boolean isRequest;
	int characterCount;
	
	public RequestPartitionRule(IToken token, boolean isRequest) {
		this.token = token;
		this.isRequest = isRequest;
	}
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		if(scanner.getColumn() != 0)
			return Token.UNDEFINED;
		characterCount = 0;
		if(!parseKeyword(scanner) || !parseRemaining(scanner)) {
			rewind(scanner);
			return Token.UNDEFINED;
		}
		return token;
	}
	
	private boolean parseKeyword(ICharacterScanner scanner) {
		final StringBuilder buffer = new StringBuilder();
		while(true) {
			int c = scanner.read();
			if(c == ICharacterScanner.EOF) 
				return false;
		
			characterCount++;
			if(c == '\r' || c == '\n') {
				return false;
			}
			if(c == ':')
				return false;
			if(!isRequest && c == '/')
				return buffer.toString().equals("HTTP");
			
			if(c == ' ') 
				return matchKeyword(buffer.toString());
			buffer.append((char)c);
			
		}
	}
	
	private boolean parseRemaining(ICharacterScanner scanner) {
		while(true) {
			int c = scanner.read();
			if(c == ICharacterScanner.EOF)
				return false;
			characterCount++;
			if(c == '\n')
				return true;
		}		
	}
	private void rewind(ICharacterScanner scanner) {
		for(int i = 0; i < characterCount; i++)
			scanner.unread();
	}
	
	private boolean matchKeyword(String word) {
		if(!isRequest)
			return false;
		for(String k : RequestMethodWordDetector.methods)
			if(k.equalsIgnoreCase(word))
				return true;
		return false;
	}
}