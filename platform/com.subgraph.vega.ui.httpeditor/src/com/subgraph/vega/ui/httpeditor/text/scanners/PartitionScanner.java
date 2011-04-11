package com.subgraph.vega.ui.httpeditor.text.scanners;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class PartitionScanner implements IPartitionTokenScanner {
	public final static String HTTP_HEADERS = "http-headers";
	public final static String HTTP_BODY = "http-body";
	public final static String[] TYPES = { HTTP_HEADERS, HTTP_BODY };
	private final static int EOF = -1;
	private IDocument document;
	private int offset;
	private int rangeEnd;
	private int tokenOffset;
	private String partitionName;
	private int partitionOffset;
	private IToken lastToken;
	
	private final IToken headersToken = new Token(HTTP_HEADERS);
	private final IToken bodyToken = new Token(HTTP_BODY);
	
	@Override
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		setResumeState(contentType, partitionOffset);
		this.document = document;
		this.offset = offset;
		this.rangeEnd = offset + length;
		lastToken = null;
	}

	private void setResumeState(String partitionName, int partitionOffset) {
		this.partitionName = partitionName;
		this.partitionOffset = partitionOffset;
	}
	
	private void resetResumeState() {
		setResumeState(null, -1);
	}
	
	private boolean hasResumeState() {
		return partitionName != null && partitionOffset >= 0;
	}
	
	@Override
	public int getTokenLength() {
		if(offset < rangeEnd)
			return offset - tokenOffset;
		else
			return rangeEnd - tokenOffset;
	}

	@Override
	public int getTokenOffset() {
		return tokenOffset;
	}

	@Override
	public IToken nextToken() {
		if(hasResumeState()) {
			lastToken = nextResumedToken();
			return lastToken;
		}
		
		tokenOffset = offset;
		
		if(isEOF()) {
			return Token.EOF;
		} else if(lastToken == null) {
			while(!isEOF()) {
				int length = scanLine();
				if(length == 0)
					break;
			}
			lastToken = headersToken;
			return headersToken;
			
		} else if(lastToken.equals(headersToken)) {
			offset = rangeEnd;
			lastToken = bodyToken;
			return bodyToken;
		} else {
			throw new IllegalStateException();
		}
		
	}

	private IToken nextResumedToken() {
		if(!hasResumeState())
			throw new IllegalStateException();
		
		tokenOffset = partitionOffset;
		
		if(partitionName.equals(HTTP_HEADERS)) {
			while(!isEOF()) {
				int lineLength = scanLine();
				if(lineLength == 0) 
					break;
			}
			resetResumeState();
			return headersToken;
		} else if(partitionName.equals(HTTP_BODY)) {
			offset = rangeEnd;
			resetResumeState();
			return bodyToken;
		} else {
			return null;
		}
	}
	@Override
	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, -1);
	}
	
	/* 
	 * Scan to the end of the current line and return the length 
	 * excluding the line delimiters (either "\n" or "\r\n")
	 */
	private int scanLine() {
		int start = offset;
		int lastChar = -1;
		while(!isEOF()) {
			int c = read();
			if(c == EOF)
				break;
			if(c == '\n') {
				if(lastChar == '\r')
					return offset - start - 2;
				else
					return offset - start - 1;
			}
			lastChar = c;
		}
		return offset - start;
	}
	
	private int read() {
		if(offset < rangeEnd) {
			try {
				return document.getChar(offset++);
			} catch (BadLocationException e) { }
		}
		return EOF;	
	}
	
	private boolean isEOF() {
		return offset >= rangeEnd;
	}

}