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

import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class HexEditLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider  {

	private final HexEditFonts fonts;
	
	HexEditLabelProvider(HexEditFonts fonts) {
		this.fonts = fonts;
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof HexEditModelItem))
			return null;
		final HexEditModelItem item = (HexEditModelItem) element;
		if(columnIndex == 0)
			return String.format("%04X:", item.getOffset());
		else if(columnIndex > 0 && columnIndex <= item.getRowLength()) {
			int value = item.getByteAt(columnIndex - 1);
			if(value == -1)
				return "";
			else
				return String.format("%02X", value);
		} else if(columnIndex == item.getRowLength() + 1) {
			return renderAscii(item);
		} else {
			return "???";
		}
	}
	
	private String renderAscii(HexEditModelItem item) {
		final StringBuilder buffer = new StringBuilder();
		final int len = item.getRowByteCount();
		final byte[] rowData = new byte[len];
		item.getData(rowData);
		for(int i = 0; i < item.getRowLength(); i++) {
			if(i >= rowData.length)
				buffer.append(" ");
			else
				buffer.append(renderByte(rowData[i]));
		}
		return buffer.toString();
	}
	
	private String renderByte(byte b) {
		if(isPrintable(b))
			return Character.toString((char)b);
		else 
			return ".";
	}
	
	private boolean isPrintable(byte b) {
		return (b >= 32 && b <= 126);
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		if(!(element instanceof HexEditModelItem))
			return null;
		final HexEditModelItem item = (HexEditModelItem) element;
		if(columnIndex <= item.getRowLength())
			return fonts.getElementFont();
		else
			return fonts.getAsciiFont();
	}	
}
