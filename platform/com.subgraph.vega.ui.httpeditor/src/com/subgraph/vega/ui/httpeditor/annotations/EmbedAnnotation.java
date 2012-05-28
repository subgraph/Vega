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
package com.subgraph.vega.ui.httpeditor.annotations;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.GC;

public abstract class EmbedAnnotation extends Annotation {

	private final int height;
	
	private String cachedPaddingString;
	
	protected EmbedAnnotation(String type, int height) {
		super(type, false, "");
		this.height = height;
		
	}

	private String generatePaddingString(TextViewer viewer) {
		final GC gc = new GC(viewer.getTextWidget());
		final StringBuilder sb = new StringBuilder();
		gc.setFont(viewer.getTextWidget().getFont());
		while(gc.textExtent(sb.toString()).y < height) {
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public String getPaddingString(TextViewer viewer) {
		if(cachedPaddingString == null) {
			cachedPaddingString = generatePaddingString(viewer);
		}
		return cachedPaddingString;
	}
}
