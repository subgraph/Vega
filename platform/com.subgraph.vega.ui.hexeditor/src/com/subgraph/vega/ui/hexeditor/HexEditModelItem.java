package com.subgraph.vega.ui.hexeditor;

public class HexEditModelItem {
	private final int offset;
	private final byte[] data;
	
	HexEditModelItem(int offset, byte[] data) {
		if(data.length > HexEditModel.ROW_LENGTH)
			throw new IllegalArgumentException();
		this.offset = offset;
		this.data = data;
	}
	
	int getOffset() {
		return offset;
	}
	
	int getByteAt(int index) {
		if(index < 0 || index >= HexEditModel.ROW_LENGTH)
			throw new IllegalArgumentException();
		
		if(index >= data.length)
			return -1;
		
		return data[index] & 0xFF;
	}
	
	byte[] getData() {
		return data;
	}

}