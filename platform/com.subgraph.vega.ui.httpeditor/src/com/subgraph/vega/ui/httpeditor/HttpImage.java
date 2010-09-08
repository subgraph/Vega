package com.subgraph.vega.ui.httpeditor;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import com.subgraph.vega.ui.httpeditor.hover.IHttpImage;

public class HttpImage implements IHttpImage {

	private final Device device;
	private final ImageData imageData;
	private final String imageName;
	private final int imageSize;
	
	private Image instance;
	
	public HttpImage(Device dev, ImageData data, String name, int size) {
		device = dev;
		imageData = data;
		imageName = name;
		imageSize = size;
	}
	
	@Override
	public int getImageByteSize() {
		return imageSize;
	}

	@Override
	public ImageData getImageData() {
		return imageData;
	}

	@Override
	public String getImageName() {
		return imageName;
	}

	@Override
	public void dispose() {
		if(instance != null)
			instance.dispose();		
	}

	@Override
	public Image getImageInstance() {
		instance = new Image(device, imageData);
		return instance;
	}
}