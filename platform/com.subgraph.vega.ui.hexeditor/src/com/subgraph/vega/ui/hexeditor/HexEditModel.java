package com.subgraph.vega.ui.hexeditor;

public class HexEditModel {
	final static int ROW_LENGTH = 16;
	
	private final byte[] binaryData;
	private final int lineCount;
	
	public HexEditModel(byte[] binaryData) {
		this.binaryData = binaryData;
		lineCount = (binaryData.length + (ROW_LENGTH - 1)) / ROW_LENGTH;
		
	}
	
	int getLineCount() {
		return lineCount;
	}
	
	HexEditModelItem getItemForLine(int line) {
		if(line >= lineCount)
			throw new IllegalArgumentException();
		if(line == lineCount - 1)
			return getItemForLastLine();
		
		final byte[] lineData = new byte[ROW_LENGTH];
		int lineOffset = ROW_LENGTH * line;
		System.arraycopy(binaryData, lineOffset, lineData, 0, ROW_LENGTH);
		return new HexEditModelItem(lineOffset, lineData);
			
	}
	
	private HexEditModelItem getItemForLastLine() {
		int lastLineOffset = (ROW_LENGTH * (lineCount - 1));
		int lastLineLength = binaryData.length - lastLineOffset;
		byte[] lineData = new byte[lastLineLength];
		System.arraycopy(binaryData, lastLineOffset, lineData, 0, lastLineLength);
		return new HexEditModelItem(lastLineOffset, lineData);
		
	}
}