package com.subgraph.vega.ui.httpeditor.text.dom;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;

import com.subgraph.vega.ui.httpeditor.text.annotation.Base64DataAnnotation;

public class Base64Parser {
	private final static int MINIMUM_BASE64_LENGTH = 10;
	private final LineScanner scanner;
	private final IAnnotationModel annotationModel;
	private final StringBuilder buffer;
	private boolean debug = false;
	private int slashCount;
	private int dashCount;
	private int underlineCount;
	private int plusCount;
	private int digitSymbolCount;
	private int upperCount;
	private int lowerCount;
	
	Base64Parser(LineScanner scanner, IAnnotationModel model) {
		this.scanner = scanner;
		this.annotationModel = model;
		buffer = new StringBuilder();
	}
	
	void parse() {
		scanner.resetScanner();
		while(!scanner.isEOF()) {
			processBase64Line();
			scanner.nextLine();
		}
	}
	
	private void resetBuffer() {
		buffer.setLength(0);
		digitSymbolCount = 0;
		upperCount = 0;
		lowerCount = 0;
		slashCount = 0;
		dashCount = 0;
		underlineCount = 0;
		plusCount = 0;
	}
	
	private void processBase64Line() {
		resetBuffer();
		
		while(!scanner.isEOL()) {
			if(buffer.length() == 0)
				scanner.markTokenStartOffset();
			processOneCharacter();
		}
		
		if(buffer.length() > 0)
			processBuffer();
	}
	
	private void processOneCharacter() {
		int c = scanner.read();
		if(isBase64Character(c))
			buffer.append((char)c);
		else if(buffer.length() > 0)
			finalizeBase64String(c);
		else
			resetBuffer();
		
	}
	
	private void finalizeBase64String(int c) {
		if(c == '=') {
			buffer.append('=');
			if(scanner.read() == '=') 
				buffer.append('=');
			else
				scanner.unread();
		}
		processBuffer();
	}
	
	private boolean isSymbol(int c) {
		switch(c) {
		case '/':
			slashCount++;
			return true;
		case '-':
			dashCount++;
			return true;
		case '+':
			plusCount++;
			return true;
		case '_':
			underlineCount++;
			return true;
		default:
			return false;
		}
	}
	private boolean isDigitOrSymbol(int c) {
		if((c >= '0' && c <= '9') || isSymbol(c)) {
			digitSymbolCount++;
			return true;
		} else
			return false;
	}
	
	private boolean isLower(int c) {
		if(c >= 'a' && c <= 'z') {
			lowerCount++;
			return true;
		} else
			return false;
	}
	
	private boolean isUpper(int c) {
		if(c >= 'A' && c <= 'Z') {
			upperCount++;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isBase64Character(int c) {
		return isLower(c) || isUpper(c) || isDigitOrSymbol(c);
	}
	
	private void processBuffer() {
		if(buffer.length() >= MINIMUM_BASE64_LENGTH && processCounts()) {
			boolean isUrlSafe = (underlineCount + dashCount) > 0;
			Base64DataAnnotation a = new Base64DataAnnotation(buffer.toString(), isUrlSafe);
			Position p = new Position(scanner.getTokenStartOffset(), buffer.length());
			annotationModel.addAnnotation(a, p);
		}
		resetBuffer();
		
	}
	
	private boolean processCounts() {
		if((plusCount + slashCount > 0) && (underlineCount + dashCount) > 0)
			return false;
		
		final int total = upperCount + lowerCount + digitSymbolCount;
		if(debug)
			printRatios(total);
		return 
				range(upperCount, total, 25, 60) &&
				range(lowerCount, total, 25, 60) &&
				range(digitSymbolCount, total, 5, 35) &&
				range(plusCount, total, 0, 6) &&
				range(slashCount, total, 0, 6) &&
				range(underlineCount, total, 0, 6) &&
				range(dashCount, total, 0, 6);
		
	}
	
	
	private void printRatios(int total) {
		int upperRatio = getRatio(upperCount, total);
		int lowerRatio = getRatio(lowerCount, total);
		int digitRatio = getRatio(digitSymbolCount, total);
		int plusRatio = getRatio(plusCount, total);
		int slashRatio = getRatio(slashCount, total);
		int ulRatio = getRatio(underlineCount, total);
		int dashRatio = getRatio(dashCount, total);
		System.out.print("b64: "+ buffer.toString());
		System.out.print(" (U: "+ upperRatio +" L: "+ lowerRatio +" D: "+ digitRatio);
		System.out.println(" S: "+ plusRatio +"/"+ slashRatio +"/"+ ulRatio +"/"+ dashRatio +")");
	}
	
	private int getRatio(int count, int total) {
		return (int) Math.round( ((double) count) / ((double) total) * 100.0);
	}
	
	private boolean range(int count, int total, int min, int max) {
		final int percent = getRatio(count, total);
		return (percent <= max && percent >= min);
	}

}