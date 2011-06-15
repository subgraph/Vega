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
package com.subgraph.vega.ui.hexeditor;

public class HexEditModel {
	final static int DEFAULT_ROW_LENGTH = 16;
	
	private final byte[] binaryData;
	private final int rowLength;
	private final int lineCount;
	
	private boolean dirtyFlag;
	
	public HexEditModel(byte[] binaryData) {
		this(binaryData, DEFAULT_ROW_LENGTH, false);
	}

	public HexEditModel(byte[] binaryData, int rowLength) {
		this(binaryData, rowLength, false);
	}
	
	public HexEditModel(byte[] binaryData, int rowLength, boolean dirtyFlag) {
		if(rowLength <= 0)
			throw new IllegalArgumentException();
		this.binaryData = binaryData;
		this.rowLength = rowLength;
		this.lineCount = (binaryData.length + (rowLength - 1)) / rowLength;
		this.dirtyFlag = dirtyFlag;
	}

	byte[] getContent() {
		return binaryData;
	}

	boolean isDirty() {
		return dirtyFlag;
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
	
	void markDirty() {
		dirtyFlag = true;
	}

	HexEditModel getModelForRowLength(int rowLength) {
		return new HexEditModel(binaryData, rowLength, dirtyFlag);
	}

	HexEditModelItem getItemForLine(int line) {
		if(line >= lineCount)
			throw new IllegalArgumentException();
		
		int lineOffset = rowLength * line;
		if(line == lineCount - 1) {
			int lastLineLength = binaryData.length - lineOffset;
			return new HexEditModelItem(this, lineOffset, binaryData, lastLineLength, rowLength);
		}
		return new HexEditModelItem(this, lineOffset, binaryData, rowLength, rowLength);
	}
}
