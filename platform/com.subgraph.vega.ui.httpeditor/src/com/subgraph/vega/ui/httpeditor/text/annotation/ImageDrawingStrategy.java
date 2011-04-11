package com.subgraph.vega.ui.httpeditor.text.annotation;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter.IDrawingStrategy;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class ImageDrawingStrategy implements IDrawingStrategy {
	private final SourceViewer viewer;
	
	public ImageDrawingStrategy(SourceViewer viewer) {
		this.viewer = viewer;
	}
	
	@Override
	public void draw(Annotation annotation, GC gc, StyledText textWidget,
			int offset, int length, Color color) {
		if(gc != null)
			renderImage(annotation, gc, textWidget, offset, length, color);
		else
			textWidget.redrawRange(offset, length, true);
	}
	
	private void renderImage(Annotation annotation, GC gc, StyledText textWidget, int offset, int length, Color color) {
		final Position position = viewer.getAnnotationModel().getPosition(annotation);
		final Image image = ((ImageAnnotation) annotation ).getImage();
		if(image == null)
			return;
		final int renderOffset = (position == null) ? (offset) : (position.offset);
		final Point left = textWidget.getLocationAtOffset(renderOffset);
		if(position == null && length > 0) {
			final Point right = textWidget.getLocationAtOffset(renderOffset + length);
			if(left.x > right.x) {
				// hack: sometimes linewrapping text widget gives us the wrong x/y for the first character of a line that
				// has been wrapped
				left.x = 0;
				left.y = right.y;
			}
		}
		// fill the background first so that drawing works properly when
		// the viewer itself is not redrawing and image contains semi-transparent
		// regions
		final Color foreground = gc.getForeground();
		final Color background = gc.getBackground();
		gc.setForeground(textWidget.getBackground());
		gc.setBackground(textWidget.getBackground());
		final Rectangle bounds = image.getBounds();
		gc.fillRectangle(new Rectangle(left.x, left.y, bounds.width, bounds.height));
		
		// now draw the image
		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.drawImage(image, left.x, left.y);
	}

}
