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
package com.subgraph.vega.ui.httpeditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Position;

public class HeaderDecoder {
	private final static String DECODED_CHAR_CATEGORY = "__decoded_char_category";
	private final HttpMessageDocument messageDocument;
	private boolean decodeState = false;
	
	HeaderDecoder(HttpMessageDocument messageDocument) {
		this.messageDocument = messageDocument;
		this.messageDocument.getDocument().addPositionCategory(DECODED_CHAR_CATEGORY);
		this.messageDocument.getDocument().addPositionUpdater(new DefaultPositionUpdater(DECODED_CHAR_CATEGORY));
	}

	public void toggleDecodeState() {
		decodeState = !decodeState;
		if(decodeState) {
			decodeHeaders();
		} else {
			undecodeHeaders();
		}
	}
	
	public void decodeHeaders() {
		try {
			decode();
		} catch (BadLocationException e) {
			throw new RuntimeException("Bad location in document decoding header", e);
		} catch (BadPositionCategoryException e) {
			throw new RuntimeException("Position category does not exist in HeaderDecoder", e);
		}
	}
	
	public void undecodeHeaders() {
		try {
			encode();
		} catch (BadPositionCategoryException e) {
			throw new RuntimeException("Bad location in document decoding header", e);
		} catch (BadLocationException e) {
			throw new RuntimeException("Position category does not exist in HeaderDecoder", e);
		}
	}
	
	public String getUndecodedHeaderContent() {
		try {
			if(!decodeState) {
				return getHeaderSectionText();
			}
			return getEncodedHeaderSectionText();
		} catch (BadLocationException e) {
			throw new RuntimeException("Bad location getting undecoded header content");
		}
	}
	
	private String getHeaderSectionText() throws BadLocationException {
		final Position headerSection = messageDocument.getHeaderSection();
		return messageDocument.getDocument().get(headerSection.getOffset(), headerSection.getLength());
	}
	
	private String getEncodedHeaderSectionText() throws BadLocationException {
		final Position headerSection = messageDocument.getHeaderSection();
		final int start = headerSection.getOffset();
		final int end = start + headerSection.getLength();
		final StringBuilder sb = new StringBuilder();
		for(int i = start; i < end; i++) {
			char c = getCharAt(i);
			if(messageDocument.getDocument().containsPosition(DECODED_CHAR_CATEGORY, i, 1)) {
				sb.append(getEncodedCharacter(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private void decode() throws BadLocationException, BadPositionCategoryException {
		final Position headerPosition = messageDocument.getHeaderSection();
		final Position remaining = new Position(headerPosition.getOffset(), headerPosition.getLength());
		while(remaining.getLength() > 0) {
			char c = getCharAt(remaining.getOffset());
			consumeRemaining(remaining, 1);
			if(c == '%') {
				processPercent(remaining);
			}
		}
	}

	private void processPercent(Position remaining) throws BadLocationException, BadPositionCategoryException {
		if(remaining.getLength() <= 0) {
			return;
		}
		final char c1 = getCharAt(remaining.getOffset());
		if(c1 == '%') {
			replaceDecodedChar(remaining.getOffset() - 1, 2, '%');
			remaining.length -= 1;
			return;
		}
		if(!isHex(c1) || remaining.getLength() == 1) {
			return;
		}
		final char c2 = getCharAt(remaining.getOffset() + 1);
		if(!isHex(c2)) {
			return;
		}
		final char cc = hexDecode(c1,c2);
		replaceDecodedChar(remaining.getOffset() - 1, 3, cc);
		remaining.length -= 2;
	}
	
	private void consumeRemaining(Position remaining, int count) {
		remaining.offset += count;
		remaining.length -= count;
	}
	
	private void replaceDecodedChar(int offset, int length, char c) throws BadLocationException, BadPositionCategoryException {
		messageDocument.getDocument().replace(offset, length, String.valueOf(c));
		messageDocument.getDocument().addPosition(DECODED_CHAR_CATEGORY, new Position(offset, 1));
	}

	private char getCharAt(int offset) throws BadLocationException {
		return messageDocument.getDocument().getChar(offset);
	}
	
	private final String HEX_DIGITS = "01234567890abcdef";

	private boolean isHex(char c) {
		return (HEX_DIGITS.indexOf(Character.toLowerCase(c)) != -1);
	}

	private char hexDecode(char hi, char low) {
		StringBuilder sb = new StringBuilder();
		sb.append(hi);
		sb.append(low);
		return (char) Integer.parseInt(sb.toString(), 16);
	}

	private void encode() throws BadPositionCategoryException, BadLocationException {
		final Position[] positions = messageDocument.getDocument().getPositions(DECODED_CHAR_CATEGORY);
		for(Position p: positions) {
			char c = getCharAt(p.getOffset());
			messageDocument.getDocument().replace(p.getOffset(), p.getLength(), getEncodedCharacter(c));
			messageDocument.getDocument().removePosition(DECODED_CHAR_CATEGORY, p);
		}
	}
	
	private String getEncodedCharacter(char c) {
		if(c == '%') {
			return "%%";
		} else {
			return "%" + hexEncode(c);
		}
	}
	
	private String hexEncode(char c) {
		return String.format("%02X", (int)c);
	}
}
