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
package com.subgraph.vega.ui.httpeditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Colors implements ISharedTextColors {
	private static RGB c(int rgb) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new RGB(r, g, b);
	}
	public static RGB HIGHLIGHT_SEARCH_MATCH = c(0xFFFF99);
	public static RGB ALERT_HIGHLIGHT = c(0xFFA0A0);
	public static RGB NO_SEARCH_RESULT_TEXT_BACKGROUND = c(0xFF7070);
	
	public static RGB HEADER_NAME = c(0x79693E);
	public static RGB REQUEST_VERB = c(0x3F3415);
	public static RGB HTTP_VERSION = c(0x4D4122);
	
	public static RGB STRING = c(0x4B4F72);
	public static RGB INTEGER = c(0x48592C);
	public static RGB ENCODED_CHAR = c(0x7D4C63);
	public static RGB DATE = c(0x4271ae);
	
	public static RGB MULTI_LINE_COMMENT = c(0x4B4F92);
	public static RGB SINGLE_LINE_COMMENT = c(0x8e908c);
	public static RGB TAG = c(0x8959a8);
	public static RGB KEYWORD = c(0x8959a8);
	public static RGB OTHER = c(0x4d4d4c);
	
	private final Map<RGB, Color> colorMap = new HashMap<RGB, Color>();
	private final Display display;
	
	public Colors(Display display) {
		this.display = display;
	}
	
	public void dispose() {
		for(Color c: colorMap.values())
			c.dispose();
	}
	
	public Color get(RGB rgb) {
		if(!colorMap.containsKey(rgb)) {
			colorMap.put(rgb, new Color(display, rgb));
		}
		return colorMap.get(rgb);
	}

	@Override
	public Color getColor(RGB rgb) {
		return get(rgb);
	}
}
