package com.subgraph.vega.ui.httpeditor.parser;

import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

public abstract class ParserBase {

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

		if (idxLf > 0) {
			if (src.charAt(idxLf - 1) == HTTP.CR) {
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
				if (src.charAt(idxLf - 1) == HTTP.CR) {
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
        int blank = lnBuf.indexOf(' ', idxPos, lnCursor.getUpperBound());
        if (blank < 0) {
        	return null;
        }
		lnCursor.updatePos(blank);
		return lnBuf.substringTrimmed(idxPos, blank);		
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

}
