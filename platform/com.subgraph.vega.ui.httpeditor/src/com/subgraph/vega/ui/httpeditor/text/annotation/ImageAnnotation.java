package com.subgraph.vega.ui.httpeditor.text.annotation;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Image;

public class ImageAnnotation extends Annotation {
	public final static String TYPE = "image";
	private final Image image;
	
	public ImageAnnotation(Image image) {
		super(TYPE, false, null);
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}
}
