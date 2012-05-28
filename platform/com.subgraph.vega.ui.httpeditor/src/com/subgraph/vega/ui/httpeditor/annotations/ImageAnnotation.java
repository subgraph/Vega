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

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class ImageAnnotation extends EmbedAnnotation implements ISelfDrawingAnnotation {
	public final static String TYPE = "com.subgraph.vega.ui.httpviewer.annotation.image";
	private final Image image;
	
	public ImageAnnotation(Image image) {
		super(TYPE, image.getBounds().height);
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}

	@Override
	public void draw(GC gc, StyledText textControl, int offset, int length) {
		if (gc != null) {
			if (image != null) {
				Point left = textControl.getLocationAtOffset(offset);
				if (length > 0) {
					Point right = textControl.getLocationAtOffset(offset + length);
					if (left.x > right.x) {
						// hack: sometimes linewrapping text widget gives us the wrong x/y for the first character of a line that
						// has been wrapped.
						left.x = 0;
						left.y = right.y;
					}
				}

				// fill the background first so that drawing works properly when
				// the viewer itself is not redrawing and image contains semi-transparent
				// regions
				Color foreground = gc.getForeground();
				Color background = gc.getBackground();
				gc.setForeground(textControl.getBackground());
				gc.setBackground(textControl.getBackground());
				Rectangle bounds = image.getBounds();
				gc.fillRectangle(new Rectangle(left.x, left.y, bounds.width, bounds.height));

				// now draw the image.
				gc.setForeground(foreground);
				gc.setBackground(background);
				gc.drawImage(image, left.x, left.y);
			}
		} else {
			textControl.redrawRange(offset, length, true);
		}		
	}
}
