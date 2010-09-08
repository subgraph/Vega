package com.subgraph.vega.ui.httpeditor.dom;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class LineScanner {
	static final int EOL = -1;
	
	private final IDocument document;
	private int currentLine;
	private IRegion currentLineRegion;
	private int currentLineOffset;
	private boolean isEOF;
	
	private int tokenStartOffset;
	
	LineScanner(IDocument document) {
		this.document = document;
		resetScanner();
	}
	
	void resetScanner() {
		currentLine = 0;
		isEOF = false;
		nextLine();
	}
	
	void nextLine() {
		if(currentLine >= document.getNumberOfLines()) {
			currentLineRegion = null;
			currentLineOffset = 0;
			isEOF = true;
		} else {
			try {
				currentLineOffset = 0;
				currentLineRegion = document.getLineInformation(currentLine++);
			} catch (BadLocationException e) {
				throw new IndexOutOfBoundsException(e.getMessage());
			}
		}
	}
	
	boolean currentLineStartsWith(String string) {
		if(isEOF)
			return false;
		try {
			final String lineData = document.get(currentLineRegion.getOffset(), currentLineRegion.getLength());
			return lineData.startsWith(string);
		} catch (BadLocationException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}
	
	boolean matchPrefix(String prefix) {
		final String str = readStringForLength(prefix.length());
		if(str.equals(prefix))
			return true;
		unread(str.length());
		return false;
	}
	
	private String readStringForLength(int length) {
		final StringBuilder sb = new StringBuilder();
		while(sb.length() < length) {
			int c = read();
			if(c == LineScanner.EOL)
				return sb.toString();
		}
		return sb.toString();
	}
	
	int getCurrentLineOffset() {
		return currentLineRegion.getOffset();
	}
	
	int getCurrentLineLength() {
		return currentLineRegion.getLength();
	}
	
	int getOffsetIntoCurrentLine() {
		return currentLineOffset;
	}
	int getCurrentAbsoluteOffset() {
		return getCurrentLineOffset() + currentLineOffset;
	}
	
	void markTokenStartOffset() {
		tokenStartOffset = getCurrentAbsoluteOffset();
	}
	
	int getTokenStartOffset() {
		return tokenStartOffset;
	}
	
	int getTokenLength() {
		return getCurrentAbsoluteOffset() - tokenStartOffset;
	}
	
	boolean isEOF() {
		return isEOF;
	}
	
	boolean isEOL() {
		return isEOF || currentLineOffset >= currentLineRegion.getLength();
	}
	
	int read() {
		if(isEOF || currentLineOffset >= currentLineRegion.getLength())
			return EOL;
		try {
			return document.getChar(currentLineRegion.getOffset() + currentLineOffset++);
		} catch (BadLocationException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}
	
	void unread() {
		unread(1);
	}
	
	void unread(int count) {
		if(currentLineOffset < count)
			throw new IllegalStateException();
		currentLineOffset -= count;
	}
}
	
