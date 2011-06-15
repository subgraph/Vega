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

public class HexEditModelItem {
	private final HexEditModel model;
	private final int itemOffset;
	private final byte[] data;
	private final int rowCount;
	private final int rowLength;
	
	
	HexEditModelItem(HexEditModel model, int offset, byte[] data, int rowCount, int rowLength) {
		this.model = model;
		this.itemOffset = offset;
		this.data = data;
		// XXX We can calculate this, right?
		this.rowCount = rowCount;
		this.rowLength = rowLength;
	}
	
	int getOffset() {
		return itemOffset;
	}
	
	int getRowByteCount() {
		return rowCount;
	}
	int getRowLength() {
		return rowLength;
	}

	int getByteAt(int index) {
		if(index < 0 || index >= rowLength)
			throw new IllegalArgumentException();
		
		if(index + itemOffset >= data.length)
			return -1;
		
		return data[index + itemOffset] & 0xFF;
	}
	
	void setByteAt(int index, int value) {
		if(index < 0 || index >= rowCount)
			throw new IllegalArgumentException();
		data[index + itemOffset] = (byte) value;
		model.markDirty();
	}

	void getData(byte[] buffer) {
		getData(buffer, 0);
	}

	void getData(byte[] buffer, int offset) {
		if((buffer.length - offset) < rowCount)
			throw new IllegalArgumentException();
		System.arraycopy(data, itemOffset, buffer, offset, rowCount);
	}
}
