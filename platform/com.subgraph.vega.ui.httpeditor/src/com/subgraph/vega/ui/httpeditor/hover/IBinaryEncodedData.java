package com.subgraph.vega.ui.httpeditor.hover;

public interface IBinaryEncodedData {
	String getRawEncodedData();
	byte[] getDecodedBytes();
	boolean isPrintable();
	String getPrintableRepresentation();
	String getDescription();
}