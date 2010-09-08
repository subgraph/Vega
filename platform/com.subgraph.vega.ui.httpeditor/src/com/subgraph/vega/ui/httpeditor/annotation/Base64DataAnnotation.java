package com.subgraph.vega.ui.httpeditor.annotation;

import org.eclipse.jface.text.source.Annotation;

public class Base64DataAnnotation extends Annotation {
	public final static String TYPE = "base64-data";
	
	private final String encodedData;
	private final boolean isUrlSafeEncoding;
	
	public Base64DataAnnotation(String encodedData, boolean isUrlSafe) {
		super(TYPE, false, null);
		this.encodedData = encodedData;
		this.isUrlSafeEncoding = isUrlSafe;
	}
	
	public String getEncodedData() {
		return encodedData;
	}

	public boolean isUrlSafeEncoding() {
		return isUrlSafeEncoding;
	}
}