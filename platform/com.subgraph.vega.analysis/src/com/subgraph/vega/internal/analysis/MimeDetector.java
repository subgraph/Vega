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
package com.subgraph.vega.internal.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.subgraph.vega.api.analysis.MimeType;
import com.subgraph.vega.api.http.requests.IHttpResponse;

public class MimeDetector {
	private static final List<String> genericAsciiPrefixes = new ArrayList<String>();
	private static final Map<String, MimeType> nameMap = new HashMap<String, MimeType>();
	
	static {
		genericAsciiPrefixes.addAll(Arrays.asList("text/x-", "text/vnd.", "application/x-httpd-"));
		for(MimeType mt: MimeType.values())
			nameMap.put(mt.getCanonicalName(), mt);
		addExtraNames(MimeType.MIME_ASC_GENERIC, "text/csv");
		addExtraNames(MimeType.MIME_ASC_JAVASCRIPT, "application/x-javascript", "application/json", "text/javascript"); 
		addExtraNames(MimeType.MIME_ASC_RTF, "application/rtf");
		addExtraNames(MimeType.MIME_XML_GENERIC, "application/xml");
		addExtraNames(MimeType.MIME_IMG_BMP, "image/bmp", "image/x-icon");
		addExtraNames(MimeType.MIME_AV_WAV, "audio/wav");
		addExtraNames(MimeType.MIME_AV_RA, "audio/x-pn-realaudio", "audio/x-realaudio");
		addExtraNames(MimeType.MIME_AV_MPEG, "video/mp4");
		addExtraNames(MimeType.MIME_AV_FLV, "video/x-flv");
		addExtraNames(MimeType.MIME_AV_WMEDIA, "audio/x-ms-wma", "video/x-ms-asf");
		addExtraNames(MimeType.MIME_BIN_ZIP, "application/x-zip-compressed");
		addExtraNames(MimeType.MIME_BIN_GZIP, "application/x-gunzip", "application/x-tar-gz");
		addExtraNames(MimeType.MIME_BIN_GENERIC, "application/octet-stream");
	}

	static void addExtraNames(MimeType mime, String ... names) {
		for(int i = 0; i < names.length; i++)
			nameMap.put(names[i], mime);
	}
		
	private final CSSDetector cssDetector = new CSSDetector();
	private final JavascriptDetector jsDetector = new JavascriptDetector();
	
	MimeType getDeclaredMimeType(IHttpResponse response) {
		if(response.getRawResponse().containsHeader("Content-Type"))
			return headerToMimeType(response.getRawResponse().getFirstHeader("Content-Type"));
		return MimeType.MIME_NONE;
	}
	
	private MimeType headerToMimeType(Header hdr) {
		if(hdr == null || hdr.getValue() == null)
			return MimeType.MIME_NONE;
		
		final String ctype = hdr.getValue();
		
		if(nameMap.containsKey(ctype))
			return nameMap.get(ctype);
		for(String prefix : genericAsciiPrefixes) {
			if(ctype.startsWith(prefix))
				return MimeType.MIME_ASC_GENERIC;
		}
		return MimeType.MIME_NONE;
	}

	MimeType getSniffedMimeType(IHttpResponse response) {
		final String body = response.getBodyAsString();
		if(body == null)
			return MimeType.MIME_NONE;
		final String buffer = (body.length() > 1024) ? (body.substring(0, 1024)) : (body);
		
		if(cssDetector.isBodyCSS(response))
			return MimeType.MIME_ASC_CSS;
		else if(jsDetector.isBodyJavascript(response))
			return MimeType.MIME_ASC_JAVASCRIPT;
		else if(response.isMostlyAscii())
			return getSniffedMimeTypeForAscii(buffer);
		else
			return getSniffedMimeTypeForBinary(buffer);
	}
	
	MimeType getSniffedMimeTypeForAscii(String buffer) {
		if(buffer.startsWith("%!PS"))
			return MimeType.MIME_ASC_POSTSCRIPT;
		else if(buffer.startsWith("{\\rtf"))
			return MimeType.MIME_ASC_RTF;
		else if(buffer.startsWith("%PDF"))
			return MimeType.MIME_EXT_PDF;
		else if(buffer.contains("<OpenSearch"))
			return MimeType.MIME_XML_OPENSEARCH;
		else if(buffer.contains("<channel>") 
				|| buffer.contains("<description>") 
				|| buffer.contains("<item>") 
				|| buffer.contains("<rdf:RDF"))
			return MimeType.MIME_XML_RSS;
		else if(buffer.contains("<feed") || buffer.contains("<updated>"))
			return MimeType.MIME_XML_ATOM;
		
		final String lower = buffer.toLowerCase();
		if(lower.contains("<wml") || lower.contains("<!doctype wml "))
			return MimeType.MIME_XML_WML;
		else if(lower.contains("<cross-domain-policy>"))
			return MimeType.MIME_XML_CROSSDOMAIN;
		else if(buffer.contains("<?xml") || buffer.contains("<!DOCTYPE")) {
			if(lower.contains("<!doctype html") || buffer.contains("http://www.w3.org/1999/xhtml"))
				return MimeType.MIME_XML_XHTML;
			else
				return MimeType.MIME_XML_GENERIC;
		}
		
		final List<String> htmlStrings = Arrays.asList("<html", "<meta", "<head", "<title", "<body", "</body", "<!doctype",
				"<--", "<style", "<script", "<font", "<span", "<div", "<img", "<form", "<br", "<td", "<h1", "<li", "<p>", "href=");
		for(String s: htmlStrings) {
			if(lower.contains(s))
				return MimeType.MIME_ASC_HTML;
		}
		
		if(buffer.contains("<![CDATA[") || buffer.contains("</") || buffer.contains("/>"))
			return MimeType.MIME_XML_GENERIC;
		
		return MimeType.MIME_ASC_GENERIC;
			
				
	}
	
	MimeType getSniffedMimeTypeForBinary(String buffer) {
		final char c0 = charAt(buffer, 0);
		final char c1 = charAt(buffer, 1);
		final char c2 = charAt(buffer, 2);
		final char c3 = charAt(buffer, 3);
		
		if(c0 == 0xFF && c1 == 0xD8 && c2 == 0xFF) 
			return MimeType.MIME_IMG_JPEG;
		else if(buffer.startsWith("GIF8")) 
			return MimeType.MIME_IMG_GIF;
		else if(c0 == 0x89 && buffer.startsWith("PNG", 1)) 
			return MimeType.MIME_IMG_PNG;
		else if(buffer.startsWith("BM"))
			return MimeType.MIME_IMG_BMP;
		else if(buffer.startsWith("II") && c2 == 42)
			return MimeType.MIME_IMG_TIFF;
		else if(buffer.startsWith("RIFF")) {
			if(charAt(buffer, 8) == 'A') {
				if(charAt(buffer, 9) == 'C')
					return MimeType.MIME_IMG_ANI;
				else 
					return MimeType.MIME_AV_AVI;
			} else
				return MimeType.MIME_AV_WAV;
			
		} else if(c0 == 0 && c1 == 0 && c2 != 0 && c3 == 0) 
			return MimeType.MIME_IMG_BMP;
		else if(c0 == 0x30 && c1 == 0x26 && c2 == 0xB2)
			return MimeType.MIME_AV_WMEDIA;
		else if(c0 == 0xFF && c1 == 0xFB)
			return MimeType.MIME_AV_MP3;
		else if(c0 == 0x00 && c1 == 0x00 && c2 == 0x01 && (c3 >> 4) == 0x0B)
			return MimeType.MIME_AV_MPEG;
		else if(buffer.length() >= 4 && buffer.substring(0, 4).equalsIgnoreCase("OggS"))
			return MimeType.MIME_AV_OGG;
		else if(c0 == 0x28 && buffer.startsWith("RMF",1))
			return MimeType.MIME_AV_RA;
		else if(c0 == 0x2E && buffer.startsWith("RMF",1))
			return MimeType.MIME_AV_RV;
		else if(buffer.startsWith("free", 4) || buffer.startsWith("mdat", 4) ||
				buffer.startsWith("wide", 4) || buffer.startsWith("pnot", 4) || 
				buffer.startsWith("skip", 4) || buffer.startsWith("moov", 4))
			return MimeType.MIME_AV_QT;
		else if(buffer.startsWith("FLV"))
			return MimeType.MIME_AV_FLV;
		else if(buffer.startsWith("FCWS") || buffer.startsWith("CWS"))
			return MimeType.MIME_EXT_FLASH;
		else if(buffer.startsWith("%PDF"))
			return MimeType.MIME_EXT_PDF;
		else if(buffer.startsWith("PK") && c2 < 6 && c3 < 7) {
			if(buffer.contains("META-INF/"))
				return MimeType.MIME_EXT_JAR;
			else
				return MimeType.MIME_BIN_ZIP;
		} else if(c0 == 0xCA && c1 == 0xFE && c2 == 0xBA && c3 == 0xBE)
			return MimeType.MIME_EXT_CLASS;
		else if(buffer.length() > 512 && c0 == 0xD0 && c1 == 0xCF && c2 == 0x11 && c3 == 0xE0) {
			switch(buffer.charAt(512)) {
			case 0xEC:
				return MimeType.MIME_EXT_WORD;
			case 0xFD:
			case 0x09:
				return MimeType.MIME_EXT_EXCEL;
			case 0x00:
			case 0x0F:
			case 0xA0:
				return MimeType.MIME_EXT_PPNT;
			}
		} else if(c0 == 0x1F && c1 == 0x8B && c2 == 0x08)
			return MimeType.MIME_BIN_GZIP;
		else if(buffer.startsWith("MSCF") && charAt(buffer, 4) == 0x00)
			return MimeType.MIME_BIN_CAB;
			
		return MimeType.MIME_BIN_GENERIC;
	}
	
	char charAt(String buffer, int idx) {
		if(idx >= buffer.length())
			return 0xFFFF;
		return buffer.charAt(idx);
	}
	
}
