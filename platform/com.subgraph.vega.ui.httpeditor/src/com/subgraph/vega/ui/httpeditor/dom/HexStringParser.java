package com.subgraph.vega.ui.httpeditor.dom;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;

import com.subgraph.vega.ui.httpeditor.annotation.HexDataAnnotation;

public class HexStringParser {
	private final static int MINIMUM_HEX_STRING_LENGTH = 16;
	private final LineScanner scanner;
	private final IAnnotationModel annotationModel;
	private final StringBuilder buffer;
	
	HexStringParser(LineScanner scanner, IAnnotationModel model) {
		this.scanner = scanner;
		this.annotationModel = model;
		this.buffer = new StringBuilder();
	}
	
	void parse() {
		scanner.resetScanner();
		while(!scanner.isEOF()) {
			processLine();
			scanner.nextLine();
		}
	}
	
	private void processLine() {
		buffer.setLength(0);
		while(!scanner.isEOL()) {
			if(buffer.length() == 0)
				scanner.markTokenStartOffset();
			int c = scanner.read();
			if(isHexDigit(c)) 
				buffer.append((char) c);
			else if(buffer.length() > 0)
				processBuffer();
		}
		if(buffer.length() > 0)
			processBuffer();
	}
	
	private void processBuffer() {
		final String hexString = buffer.toString();
		if(isAcceptableHexStringLength(hexString.length())) {
			final Position p = new Position(scanner.getTokenStartOffset(), hexString.length());
			final HexDataAnnotation a = new HexDataAnnotation(hexString);
			annotationModel.addAnnotation(a, p);
		}
		buffer.setLength(0);
	}
	
	private boolean isAcceptableHexStringLength(int length) {
		return length >= MINIMUM_HEX_STRING_LENGTH && length % 2 == 0;
	}
	private boolean isHexDigit(int c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
}
