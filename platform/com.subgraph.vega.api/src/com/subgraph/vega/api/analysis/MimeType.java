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
package com.subgraph.vega.api.analysis;

public enum MimeType {
	MIME_NONE					("none"),
	MIME_ASC_GENERIC 			("text/plain"),
	MIME_ASC_HTML 				("text/html"),
	MIME_ASC_JAVASCRIPT 		("application/javascript"),
	MIME_ASC_CSS 				("text/css"),
	MIME_ASC_POSTSCRIPT			("application/postscript"),
	MIME_ASC_RTF				("text/rtf"),
	
	MIME_XML_GENERIC			("text/xml"),
	MIME_XML_OPENSEARCH			("application/opensearchdescription+xml"),
	MIME_XML_RSS				("application/rss+xml"),
	MIME_XML_ATOM				("application/atom+xml"),
	MIME_XML_WML				("text/vnd.wap.wml"),
	MIME_XML_CROSSDOMAIN		("text/x-cross-domain-policy"),
	MIME_XML_SVG				("image/svg+xml"),
	MIME_XML_XHTML				("application/xhtml+xml"),
	
	MIME_IMG_JPEG				("image/jpeg"),
	MIME_IMG_GIF				("image/gif"),
	MIME_IMG_PNG				("image/png"),
	MIME_IMG_BMP				("image/x-ms-bmp"),
	MIME_IMG_TIFF				("image/tiff"),
	MIME_IMG_ANI				("application/x-navi-animation"),
	
	MIME_AV_WAV					("audio/x-wav"),
	MIME_AV_MP3					("audio/mpeg"),
	MIME_AV_OGG					("application/ogg"),
	MIME_AV_RA					("audio/vnd.rn-realaudio"),
	
	MIME_AV_AVI					("video/avi"),
	MIME_AV_MPEG				("video/mpeg"),
	MIME_AV_QT					("video/quicktime"),
	MIME_AV_FLV					("video/flv"),
	MIME_AV_RV					("video/vnd.rn-realvideo"),
	MIME_AV_WMEDIA				("video/x-ms-wmv"),
	
	MIME_EXT_FLASH				("application/x-shockwave-flash"),
	MIME_EXT_PDF				("application/pdf"),
	MIME_EXT_JAR				("application/java-archive"),
	MIME_EXT_CLASS				("application/java-vm"),
	MIME_EXT_WORD				("application/msword"),
	MIME_EXT_EXCEL				("application/vnd.ms-excel"),
	MIME_EXT_PPNT				("application/vnd.ms-powerpoint"),
	MIME_BIN_ZIP				("application/zip"),
	MIME_BIN_GZIP				("application/x-gzip"),
	MIME_BIN_CAB				("application/vnd.ms-cab-compressed"),
	MIME_BIN_GENERIC			("application/binary");
	
	
	public static MimeType fromCanonicalName(String name) {
		final String lower = name.toLowerCase();
		for(MimeType mt: values())
			if(mt.getCanonicalName().equals(lower))
				return mt;
		return MIME_NONE;
	}

	private final String canonicalName;
	
	MimeType(String name) {
		canonicalName = name;
	}
	
	public String getCanonicalName() {
		return canonicalName;
	}
}
