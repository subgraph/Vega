package com.subgraph.vega.internal.http.requests.config.scanner;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;

import com.subgraph.vega.internal.http.requests.config.IRequestEncodingStrategy;

public class ScannerRequestEncodingStrategy implements IRequestEncodingStrategy {
	private final String PATH_ENC_CHARS = "#&=+;,!$?%";
	private final String QUERY_ENC_CHARS = "#+;,!$?%";
	private final static Logger logger = Logger.getLogger("scanner"); 

	@Override
	public RequestLine encodeRequestLine(RequestLine requestLine) {
		final String path = lineToPath(requestLine.getUri());
		final String query = lineToQuery(requestLine.getUri());
		final StringBuilder sb = new StringBuilder();
		sb.append(encodePath(path));
		if(query != null) {
			sb.append('?');
			sb.append(encodeQuery(query));
			
			
		}
		return new BasicRequestLine(requestLine.getMethod(), sb.toString(), requestLine.getProtocolVersion());
	}
	
	private String encodeQuery(String query) {
		// TODO: Quick fix to support URI-encoding UTF-8 characters, probably wrong level of abstraction, should maybe be re-factored as its own EncodingStrategy with a check earlier in the call chain
		if (containsUnicode(query)){
			try {
				return URLEncoder.encode(query, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.warning("UnsupportedEncodingException on query: " + query);
			}
		}
		return encodeString(query, QUERY_ENC_CHARS);
	}

	private String encodePath(String path) {
		return encodeString(path, PATH_ENC_CHARS);
	}

	private String encodeString(String s, String encChars) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length(); i++) {
			encodeCharIntoBuffer(s.charAt(i), encChars, sb);
		}
		return sb.toString();
	}

	
	private void encodeCharIntoBuffer(char c, String encChars, StringBuilder sb) {
		if(c <= 0x20 || c >= 0x80 || encChars.indexOf(c) != -1) {
			appendEscape(sb, c);
		} else {
			sb.append(c);
		}
	}
	
	  private final static char[] hexDigits = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static void appendEscape(StringBuilder sb, char c) {
        sb.append('%');
        sb.append(hexDigits[(c >> 4) & 0x0f]);
        sb.append(hexDigits[(c >> 0) & 0x0f]);
    }

	
	private static String lineToPath(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return line;
		}
		return line.substring(0, idx);
	}
	
	private static String lineToQuery(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return null;
		}
		return line.substring(idx + 1);
	}
	
	private static boolean containsUnicode(String line) {
		for(int i = 0; i < line.length(); i++) {
			if (line.charAt(i) > 255) {
				return true;
			}
		}
		return false;
	}
}
