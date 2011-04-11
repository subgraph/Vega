package com.subgraph.vega.ui.httpeditor.text.hover;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public interface IHttpImage {
	ImageData getImageData();
	Image getImageInstance();
	String getImageName();
	int getImageByteSize();
	void dispose();
}