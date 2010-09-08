package com.subgraph.vega.ui.httpeditor.hover;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class HexEncodedData extends AbstractEncodedData {
	
	protected HexEncodedData(String rawEncodedData) {
		super(rawEncodedData);
		initialize();
	}

	public String getDescription() {
		final int bytes = getDecodedBytes().length;
		return "Hex encoded binary data ("+ (bytes * 8) +" bits / "+
			bytes +" bytes)";
	}

	@Override
	protected byte[] decodeData() {
		try {
			return Hex.decodeHex(getRawEncodedData().toCharArray());
		} catch (DecoderException e) {
			// XXX
			e.printStackTrace();
			return null;
		}
	}

}
