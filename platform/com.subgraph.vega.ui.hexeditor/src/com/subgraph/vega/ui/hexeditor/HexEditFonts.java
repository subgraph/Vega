package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class HexEditFonts {
	private final static String HEX_ELEMENT_FONT = "hex-element";
	private final static String HEX_ASCII_FONT = "hex-ascii";
	private final static int FONT_PADDING = 6;
	private final FontRegistry fontRegistry;

	private final int offsetColumnWidth;
	private final int dataColumnWidth;
	private final int asciiColumnWordWidth;
	
	HexEditFonts(Composite parent) {
		fontRegistry = new FontRegistry();
		offsetColumnWidth = calculateOffsetColumnWidth(parent);
		dataColumnWidth = calculateDataColumnWidth(parent);
		asciiColumnWordWidth = calculateAsciiColumnWordWidth(parent);
		addFonts();
	}
	
	private void addFonts() {
		// Monospace or Monospaced font doesn't exist on Mac... All platforms should
		// have Courier, fixes bug #63
		add(HEX_ELEMENT_FONT, "Courier", 10, SWT.NORMAL);
		add(HEX_ASCII_FONT, "Courier", 10, SWT.NORMAL);
	}
	
	private void add(String tag, String name, int size, int flags) {
		fontRegistry.put(tag, new FontData[] { new FontData(name, size, flags) });
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
