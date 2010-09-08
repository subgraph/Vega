package com.subgraph.vega.ui.httpeditor.dom;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;

public class RequestParser {
	private LineScanner scanner;
	private Base64Parser b64Parser;
	private HexStringParser hexParser;

	public RequestModel parse(IDocument document, IAnnotationModel annotationModel) {
		this.scanner = new LineScanner(document);
		this.b64Parser = new Base64Parser(scanner, annotationModel);
		this.hexParser = new HexStringParser(scanner, annotationModel);
		try {
			RequestModel model = parseRequest(new RequestModel(document.getLength()));
			b64Parser.parse();
			hexParser.parse();
			return model;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}		
	}
	
	private RequestModel parseRequest(RequestModel model) {
		model.setStartLine(parseStartLine(model));
		scanner.nextLine();
		while(!scanner.isEOF()) {
			final Header h = parseHeader(model);
			if(h == null)
				break;
			model.addHeader(h);
			scanner.nextLine();
		}
		return model;
	}
	
	private StartLine parseStartLine(RequestModel model) {
		if(scanner.currentLineStartsWith("HTTP/"))
			return parseResponseLine(model);
		else
			return parseRequestLine(model);
	}
	
	private ResponseLine parseResponseLine(RequestModel model) {
		final ResponseLine responseLine = new ResponseLine(model, scanner.getCurrentLineOffset(), scanner.getCurrentLineLength());
		responseLine.setProtocolVersion( parseVersion(responseLine));
		skipWhitespace();
		responseLine.setStatusCode( parseStatusCode(responseLine));
		skipWhitespace();
		responseLine.setStatusReason(parseStatusReason(responseLine));
		return responseLine;
	}
	
	private RequestLine parseRequestLine(RequestModel model) {
		final RequestLine requestLine = new RequestLine(model, scanner.getCurrentLineOffset(), scanner.getCurrentLineLength());
		requestLine.setMethod(parseMethod(requestLine));
		skipWhitespace();
		requestLine.setURL(parseURL(requestLine));
		skipWhitespace();
		requestLine.setProtocolVersion(parseVersion(requestLine));
		return requestLine;
	}
	
	private Method parseMethod(RequestLine requestLine) {
		final int startingOffset = scanner.getCurrentAbsoluteOffset();
		final StringBuilder methodName = new StringBuilder();
		
		while(!scanner.isEOL()) {
			final int c = scanner.read();
			if(isMethodChar(c)) 
				methodName.append((char)c);
			else if(c == ' ' && methodName.length() > 0) 
				return new Method(methodName.toString(), requestLine, startingOffset, methodName.length()); 
			else 
				break;	
		}
		scanner.unread(methodName.length());
		return null;
	}
	
	private URL parseURL(RequestLine requestLine) {
		scanner.markTokenStartOffset();
		final StringBuilder urlData = new StringBuilder();
		while(!scanner.isEOL()) {
			final int c = scanner.read();
			if(isURLChar(c)) 
				urlData.append((char)c);
			else if(c == ' ' && urlData.length() > 0) 
				return new URL(urlData.toString(), requestLine, scanner.getTokenStartOffset(), urlData.length());	
			else
				break;
		}
		scanner.unread(urlData.length());
		return null;
	}
	private boolean isMethodChar(int c) {
		return c >= 'A' && c <= 'Z';
	}
	
	private boolean isURLChar(int c) {
		return !Character.isWhitespace((char)c);
	}
	private ProtocolVersion parseVersion(StartLine line) {
		final int startingOffset = scanner.getCurrentAbsoluteOffset();
		if(!scanner.matchPrefix("HTTP/"))
			return null;
		Integer major = parseNumber();
		int c = scanner.read();
		if(c != '.') {
			// XXX
			System.out.println("BOOM");
		}
		Integer minor = parseNumber();
		int length = scanner.getCurrentAbsoluteOffset() - startingOffset;
		return new ProtocolVersion(major, minor, line, startingOffset, length);		
	}
	
	private void skipWhitespace() {
		while(true) {
			int c = scanner.read();
			if(c == LineScanner.EOL)
				return;
			if(!Character.isWhitespace((char) c)) {
				scanner.unread();
				return;
			}
		}
	}
	
	private Integer parseNumber() {
		StringBuilder buffer = new StringBuilder();
		while(true) {
			int c = scanner.read();
			if(c == LineScanner.EOL)
				break;
			if(!Character.isDigit((char) c)) {
				scanner.unread();
				break;
			}
			buffer.append((char)c);
		}
		
		if(buffer.length() > 0) {
			try {
				return Integer.parseInt(buffer.toString());
			} catch(NumberFormatException e) { 
				scanner.unread(buffer.length());
			}	
		}
		return null;
	}
	
	private StatusCode parseStatusCode(ResponseLine line) {
		final int start = scanner.getCurrentAbsoluteOffset();
		Integer code = parseNumber();
		if(code == null)
			return null;
		final int length = scanner.getCurrentAbsoluteOffset() - start;
		return new StatusCode(code, line, start, length);
		
	}
	
	private StatusReason parseStatusReason(ResponseLine line) {
		final int start = scanner.getCurrentAbsoluteOffset();
		final StringBuilder buffer = new StringBuilder();
		while(!scanner.isEOL()) {
			int c = scanner.read();
			buffer.append((char) c);
		}
		if(buffer.length() == 0)
			return null;
		
		final int length = scanner.getCurrentAbsoluteOffset() - start;
		return new StatusReason(buffer.toString(), line, start, length);
		
	}
	private Header parseHeader(RequestModel model) {
		skipWhitespace();
		if(scanner.isEOL())
			return null;
		
		scanner.markTokenStartOffset();
		final StringBuilder buffer = new StringBuilder();
		while(!scanner.isEOL()) {
			int c = scanner.read();
			if(isHeaderCharacter(c))
				buffer.append((char) c);
			else if(c == ':' && buffer.length() > 0)
				break;
			else {
				scanner.unread(buffer.length());
				return null;
			}
			
		}
		Header h = new Header(buffer.toString(), model, scanner.getTokenStartOffset(), scanner.getTokenLength());
		h.setHeaderValue(parseHeaderValue(h));
		return h;
	}
	
	private Element parseHeaderValue(Header header) {
		skipWhitespace();
		scanner.markTokenStartOffset();
		final StringBuilder buffer = new StringBuilder();
		while(!scanner.isEOL()) {
			int c = scanner.read();
			buffer.append((char) c);
		}
		return new SimpleHeaderValue(buffer.toString(), header, scanner.getTokenStartOffset(), scanner.getTokenLength());
		
	}
	
	private boolean isHeaderCharacter(int c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '-');
	}
	
}
