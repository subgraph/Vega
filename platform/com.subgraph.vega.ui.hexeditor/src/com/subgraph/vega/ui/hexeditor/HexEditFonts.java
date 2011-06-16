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

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class HexEditFonts {
	private final static String HEX_ELEMENT_FONT = "hex-element";
	private final static String HEX_ASCII_FONT = "hex-ascii";
	private final static int FONT_PADDING = 10;
	private final FontRegistry fontRegistry;

	private final int offsetColumnWidth;
	private final int dataColumnWidth;
	private final int asciiColumnWordWidth;
	
	HexEditFonts(Composite parent) {
		fontRegistry = new FontRegistry();
		addFonts(parent.getDisplay());
		offsetColumnWidth = calculateOffsetColumnWidth(parent);
		dataColumnWidth = calculateDataColumnWidth(parent);
		asciiColumnWordWidth = calculateAsciiColumnWordWidth(parent);
	}
	
	private void addFonts(Display display) {
		add(display, HEX_ELEMENT_FONT, 10, "Monospace", "Courier");
		add(display, HEX_ASCII_FONT, 10, "Monospace", "Courier");
	}
	
	private void add(Display display, String tag, int size, String ...names) {
		for(String fontName: names) {
			if(fontNameExists(display, fontName)) {
				fontRegistry.put(tag, new FontData[] { new FontData(fontName, size, SWT.NORMAL) });
			}
		}
	}
	
	boolean fontNameExists(Display display, String name) {
		return display.getFontList(name, true).length > 0 || display.getFontList(name, false).length > 0;
	}
	Font getElementFont() {
		return fontRegistry.get(HEX_ELEMENT_FONT);
	}
	
	Font getAsciiFont() {
		return fontRegistry.get(HEX_ASCII_FONT);
	}
	
	int getOffsetColumnWidth() {
		return offsetColumnWidth;
	}
	
	int getDataColumnWidth() {
		return dataColumnWidth;
	}
	
	int getAsciiColumnWordWidth() {
		return asciiColumnWordWidth;
	}
	
	private int calculateOffsetColumnWidth(Composite composite) {
		final GC gc = new GC(composite);
		return getColumnWidth(HEX_ELEMENT_FONT, "000000", gc);
	}
	
	private int calculateDataColumnWidth(Composite composite) {
		final GC gc = new GC(composite);
		return getColumnWidth(HEX_ELEMENT_FONT, "00", gc);
	}
	
	private int calculateAsciiColumnWordWidth(Composite composite) {
		final GC gc = new GC(composite);
		return getColumnWidth(HEX_ASCII_FONT, "0000", gc);
	}
	
	
	private int getColumnWidth(String fontTag, String str, GC gc) {
		gc.setFont(fontRegistry.get(fontTag));
		Point extent = gc.textExtent(str);
		return extent.x + (FONT_PADDING * 2);
	}
}
