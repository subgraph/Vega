package com.subgraph.vega.ui.httpeditor.text.annotation;

import org.eclipse.jface.text.source.Annotation;

public class HexDataAnnotation extends Annotation {
	public final static String TYPE = "hex-data";
	
	private final String encodedData;
	
	public HexDataAnnotation(String encodedData) {
		super(TYPE, false, null);
		this.encodedData = encodedData;
	}
	
	public String getEncodedData() {
		return encodedData;
	}

}