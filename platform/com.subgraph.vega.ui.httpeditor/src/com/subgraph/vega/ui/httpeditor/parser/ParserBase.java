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
package com.subgraph.vega.ui.httpeditor.parser;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;

public abstract class ParserBase {

	/**
	 * Strip leading whitespace from a buffer. Lines with 0 or more SP or HT characters ending with LF or CRLF are
	 * removed. 
	 * @param buf Buffer.
	 * @param bufCursor Parser cursor for buf.
	 */
	protected void stripLeadingWhitspace(final CharArrayBuffer buf, final ParserCursor bufCursor) {
		final int idxTo = bufCursor.getUpperBound();
		int idxPos = bufCursor.getPos();
		int idxLast = idxPos;
		while (idxPos < idxTo) {
			char ch = buf.charAt(idxPos);
			if (ch == HTTP.CR) {
				if (idxTo + 1 < idxPos && buf.charAt(idxPos) == HTTP.LF) {
					idxPos += 2;
					idxLast = idxPos;
				} else {
					break;
				}
			} else if (ch == HTTP.LF) {
				idxPos++;
				idxLast = idxPos;
			} else {
				if (ch == HTTP.SP || ch == HTTP.HT) {
					idxPos++;
				} else {
					break;
				}
			}
		}
		bufCursor.updatePos(idxLast);		
	}
	
	/**
	 * Get the next line of characters from a CharArrayBuffer into another CharArrayBuffer. Treats LF and CRLF as valid
	 * line delimiters. Treats the entire buffer as a line if no line delimiters are found.
	 * 
	 * @param src Source buffer to read line from.
	 * @param srcCursor Parser cursor for src. Adjusted to discard line delimiters.
	 * @param dst Destination buffer for characters from line. 
	 * @return Number of characters in line minus line delimiters, or < 0 if none found.
	 */
	protected int readLine(final CharArrayBuffer src, final ParserCursor srcCursor, final CharArrayBuffer dst) {
		if (srcCursor.atEnd()) {
			return -1;
		}

		int idxPos = srcCursor.getPos();
		int idxLf = src.indexOf(HTTP.LF, idxPos, srcCursor.getUpperBound());
		int idxEnd;

		if (idxLf >= 0) {
			if (idxLf != 0 && src.charAt(idxLf - 1) == HTTP.CR) {
				idxEnd = idxLf - 1;
			} else {
				idxEnd = idxLf;
			}
		} else {
			idxEnd = srcCursor.getUpperBound();
			idxLf = idxEnd - 1;
		}

		dst.append(src, idxPos, idxEnd - idxPos);
		srcCursor.updatePos(idxLf + 1);
		return idxEnd - idxPos;
	}

	/**
	 * Get the next header line of characters from a CharArrayBuffer into another CharArrayBuffer. Treats LF and CRLF as
	 * valid line delimiters. Treats the entire buffer as a line if no line delimiters are found. Supports folded header
	 * field values as per the HTTP/1.1 specification.
	 *    
	 * @param src Source buffer to read line from.
	 * @param srcCursor Parser cursor for src. Adjusted to discard line delimiters.
	 * @param dst Destination buffer for characters from line. 
	 * @return Number of characters in line minus line delimiters, or < 0 if none found.
	 */
	protected int readLineHeader(final CharArrayBuffer src, final ParserCursor srcCursor, final CharArrayBuffer dst) {
		if (srcCursor.atEnd()) {
			return -1;
		}

		int idxPos = srcCursor.getPos();
		int chCnt = 0;
		int idxLf, idxEnd;

		do {		
			idxLf = src.indexOf(HTTP.LF, idxPos, srcCursor.getUpperBound());

			if (idxLf > 0) {
				if (idxLf != srcCursor.getPos() && src.charAt(idxLf - 1) == HTTP.CR) {
					idxEnd = idxLf - 1;
				} else {
					idxEnd = idxLf;
				}
			} else {
				idxEnd = srcCursor.getUpperBound();
				idxLf = idxEnd - 1;
			}

			if (chCnt != 0) {
				while (idxPos < idxEnd && (src.charAt(idxPos) == HTTP.HT || src.charAt(idxPos) == HTTP.SP)) {
					idxPos++;
				}
				if (idxPos != idxEnd) {
					dst.append(' ');
				}
			}
			
			dst.append(src, idxPos, idxEnd - idxPos);
			chCnt += idxEnd - idxPos;
			idxPos = idxLf + 1;
			srcCursor.updatePos(idxPos);
		} while (idxPos < srcCursor.getUpperBound() && (src.charAt(idxPos) == HTTP.HT || src.charAt(idxPos) == HTTP.SP));

		return chCnt;
	}

	/**
	 * Get the next word from a buffer containing a line.
	 * 
	 * @param lnBuf Buffer containing line.
	 * @param lnCursor Parser cursor for lnBuf. Adjusted to one character after the word.
	 * @return Next word, or null if none is found.
	 */
	protected String nextWord(final CharArrayBuffer lnBuf, final ParserCursor lnCursor) {
		skipSpHt(lnBuf, lnCursor);
		int idxPos = lnCursor.getPos();
        int idxLineEnd = lnBuf.indexOf(' ', idxPos, lnCursor.getUpperBound());
        if (idxLineEnd < 0) {
        	if (idxPos == lnCursor.getUpperBound()) {
        		return null;
        	}
        	idxLineEnd = lnCursor.getUpperBound();
        }
		lnCursor.updatePos(idxLineEnd);
		return lnBuf.substringTrimmed(idxPos, idxLineEnd);		
	}
	
	/**
	 * Skip SP and HT characters in a line.   
	 * 
	 * @param lnBuf Buffer containing line.
	 * @param lnCursor Parser cursor for lnBuf. Adjusted to one character after and SP and HT.
	 */
	protected void skipSpHt(final CharArrayBuffer lnBuf, final ParserCursor lnCursor) {
		int idxTo = lnCursor.getUpperBound();
		int idxPos = lnCursor.getPos();
		while (idxPos < idxTo && (lnBuf.charAt(idxPos) == HTTP.SP || lnBuf.charAt(idxPos) == HTTP.HT)) {
			idxPos++;
		}
        lnCursor.updatePos(idxPos);
	}

	/**
	 * Parse HTTP headers and add them to a IHttpMessageBuilder until a non-header line is encountered.
	 * 
	 * @param parser HC line parser.
	 * @param builder IHttpMessageBuilder to add parsed headers to.
	 * @param buf Buffer containing header data.
	 * @param bufCursor Parser cursor for buf. Adjusted to one character past the end of headers and optional CRLF line.
	 */
	protected void parseHeaders(final LineParser parser, final IHttpMessageBuilder builder, final CharArrayBuffer buf, final ParserCursor bufCursor) {
		final CharArrayBuffer lnBuf = new CharArrayBuffer(0);
		while (true) {
			lnBuf.clear();
			int idxPos = bufCursor.getPos();
			if (readLineHeader(buf, bufCursor, lnBuf) > 0) {
				try {
					// REVISIT don't want an extra step
					Header header = parser.parseHeader(lnBuf);
					builder.addHeader(header.getName(), header.getValue());
				} catch (ParseException e) {
					// for now we'll move the cursor back so the line gets treated as the start of the body
					bufCursor.updatePos(idxPos);
					return;
				}
			} else {
				break;
			}
		}
	}

}
