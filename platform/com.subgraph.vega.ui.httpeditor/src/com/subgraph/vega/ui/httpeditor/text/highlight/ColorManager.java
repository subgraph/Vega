package com.subgraph.vega.ui.httpeditor.text.highlight;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>();

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}
	
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	public IToken createHighlightingToken(RGB rgb) {
		final Color c = getColor(rgb);
		final TextAttribute ta = new TextAttribute(c);
		return new Token(ta);
	}

}