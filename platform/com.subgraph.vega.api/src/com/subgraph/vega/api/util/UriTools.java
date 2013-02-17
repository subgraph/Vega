package com.subgraph.vega.api.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;

public class UriTools {
	private final static String[] schemePrefixes = new String[] {"http://", "https://"};
	

	public static boolean isTextValidURI(String input) {
		return getURIFromText(input) != null;
	}
	
	public static URI getURIFromText(String input) {
		try {
			final URI uri = new URI(textToAbsoluteURL(input.trim()));
			if(uri.getHost() == null) {
				return null;
			}
			return uri;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static boolean doesBaseUriContain(URI base, URI uri) {
		/*
		 * Compare host names to avoid a base scope such as
		 * http://acme.com matching http://acme.computers.net
		 * 
		 * Reparse the URIs because only the 'string' field
		 * in the URI class is repopulated when URI objects
		 * are retrieved from database.
		 * 
		 * XXX create a class VegaURI which persists correctly
		 * and move this method to that class.
		 */
		final URI parsedBase = URI.create(base.toString());
		final URI parsedURI = URI.create(uri.toString());
		final String baseString = parsedBase.toString();
		return parsedBase.getHost().equalsIgnoreCase(parsedURI.getHost()) &&
				uri.toString().startsWith(baseString);
	}


	private static String textToAbsoluteURL(String input) {
		for(String prefix: schemePrefixes) {
			if(input.toLowerCase().startsWith(prefix)) {
				return input;
			}
		}
		return "http://"+ input;
	}
	
	public static URI stripQueryFromUri(URI uri) {
		try {
			return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static URI createUriFromTargetAndLine(HttpHost target, String line) {
		try {
			return new URI(target.getSchemeName().toLowerCase(), null, target.getHostName(), target.getPort(), extractPath(line), extractQuery(line), null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Failed to parse URI fields", e);
		}
	}
	
	private static String extractQuery(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return null;
		}
		return line.substring(idx + 1);
	}
	
	private static String extractPath(String line) {
		final int idx = line.indexOf('?');
		if(idx == -1) {
			return line;
		}
		return line.substring(0, idx);
	}

	
	private static boolean isHex(char c) {
		return "abcdefABCDEF0123456789".indexOf(c) != -1;
	}
	
	
	public static String removeUnicodeEscapes(String line) {
		if(line.indexOf("%u") == -1) {
			return line;
		}
		final StringBuilder sb = new StringBuilder();
		int idx = 0;
		while(idx < line.length()) {
			if(isUnicodeEscape(line, idx)) {
				splitUnicodeEscape(line, idx, sb);
				idx += 6;
			} else {
				sb.append(line.charAt(idx));
				idx += 1;
			}
		}
		return sb.toString();
	}
	
	private static boolean isUnicodeEscape(String line, int idx) {
		if(idx > (line.length() - 6)) {
			return false;
		}
		return line.charAt(idx) == '%' &&
				line.charAt(idx + 1) == 'u' &&
				isHex(line.charAt(idx + 2)) &&
				isHex(line.charAt(idx + 3)) &&
				isHex(line.charAt(idx + 4)) &&
				isHex(line.charAt(idx + 5));
	}
	
	private static void splitUnicodeEscape(String line, int idx, StringBuilder sb) {
		sb.append('%');
		sb.append(line.charAt(idx + 2));
		sb.append(line.charAt(idx + 3));
		sb.append('%');
		sb.append(line.charAt(idx + 4));
		sb.append(line.charAt(idx + 5));
	}
}
