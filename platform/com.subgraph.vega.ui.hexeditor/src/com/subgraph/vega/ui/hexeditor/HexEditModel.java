package com.subgraph.vega.ui.hexeditor;

public class HexEditModel {
	final static int DEFAULT_ROW_LENGTH = 16;
	
	private final byte[] binaryData;
	private final int rowLength;
	private final int lineCount;
	
	public HexEditModel(byte[] binaryData) {
		this(binaryData, DEFAULT_ROW_LENGTH);
	}

	public HexEditModel(byte[] binaryData, int rowLength) {
		if(rowLength <= 0)
			throw new IllegalArgumentException();
		this.binaryData = binaryData;
		this.rowLength = rowLength;
		this.lineCount = (binaryData.length + (rowLength - 1)) / rowLength;
	}

	int getOffsetForLine(int line) {
		if (line >= lineCount) {
			return 0;
		} else {
			return line * rowLength;
		}
	}

	int getLineForOffset(int offset) {
		if (offset >= binaryData.length) {
			return 0;
		} else {
			return offset / rowLength;
		}
	}

	int getLineCount() {
		return lineCount;
	}

	HexEditModel getModelForRowLength(int rowLength) {
		return new HexEditModel(binaryData, rowLength);
	}

	HexEditModelItem getItemForLine(int line) {
		if(line >= lineCount)
			throw new IllegalArgumentException();
		
		int lineOffset = rowLength * line;
		if(line == lineCount - 1) {
			int lastLineLength = binaryData.length - lineOffset;
			return new HexEditModelItem(lineOffset, binaryData, lastLineLength, rowLength);
		}
		return new HexEditModelItem(lineOffset, binaryData, rowLength, rowLength);
	}
}