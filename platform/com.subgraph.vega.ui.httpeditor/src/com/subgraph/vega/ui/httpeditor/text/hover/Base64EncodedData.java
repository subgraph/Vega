package com.subgraph.vega.ui.httpeditor.text.hover;

import org.apache.commons.codec.binary.Base64;

public class Base64EncodedData extends AbstractEncodedData {
	
	private final Base64 decoder;
	public Base64EncodedData(String encodedData, boolean isUrlSafe) {
		super(encodedData);
		decoder = new Base64(isUrlSafe);
		initialize();
	}
	
	@Override
	protected byte[] decodeData() {
		return decoder.decode(getRawEncodedData());
	}
	
	public String getDescription() {
		final int bytes = getDecodedBytes().length;
		return "Base64 encoded binary data ("+ (bytes * 8) +" bits / "+
				bytes +" bytes)";
		
	}

}