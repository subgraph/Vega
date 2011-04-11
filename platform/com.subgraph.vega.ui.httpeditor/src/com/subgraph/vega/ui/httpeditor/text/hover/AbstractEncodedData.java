package com.subgraph.vega.ui.httpeditor.text.hover;

import java.io.UnsupportedEncodingException;


public abstract class AbstractEncodedData implements IBinaryEncodedData {

	private final String encodedData;
	private byte[] binaryData;
	private String printableRepresentation;
	
	protected AbstractEncodedData(String rawEncodedData) {
		this.encodedData = rawEncodedData;
	}
	
	protected void initialize() {
		binaryData = decodeData();
		printableRepresentation = createPrintable();
	}
	
	@Override
	public String getRawEncodedData() {
		return encodedData;
	}
	
	public byte[] getDecodedBytes() {
		return binaryData;
	}
	
	public boolean isPrintable() {
		return printableRepresentation != null;
	}
	
	public String getPrintableRepresentation() {
		return printableRepresentation;
	}
	
	abstract protected byte[] decodeData();
	
	private String createPrintable() {
		if(binaryData.length == 0)
			return null;
		
		for(byte b: binaryData) {
			if(b < 32 || b > 126)
				return null;
		}
		
		try {
			return new String(binaryData, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return null;
		}

	}
}