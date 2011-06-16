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
package com.subgraph.vega.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImageCache {
	private final String pluginId;
	private final Map<ImageDescriptor, Image> imageMap = new HashMap<ImageDescriptor, Image>();
	private final Map<String, Image> disabledMap = new HashMap<String, Image>();
	
	public ImageCache(String pluginId) {
		this.pluginId = pluginId;
	}
	
	public Image get(String key) {
		return get(getDescriptor(key));
	}
	
	public Image getDisabled(String key) {
		synchronized (disabledMap) {
			final Image image = disabledMap.get(key);
			if(image == null)
				return getDisabledImageAndCache(key);
			else
				return image;
		}
	}
	
	private Image getDisabledImageAndCache(String key) {
		final Image originalImage = get(key);
		if(originalImage == null)
			return null;
		final Image newImage = new Image(originalImage.getDevice(), originalImage, SWT.IMAGE_GRAY);
		disabledMap.put(key, newImage);
		return newImage;
	}
	
	public Image get(ImageDescriptor descriptor) {
		if(descriptor == null)
			return null;
		synchronized (imageMap) {
			final Image image = imageMap.get(descriptor);
			if(image == null)
				return getImageAndCache(descriptor);
			return image;
		}
	}
	private ImageDescriptor getDescriptor(String key) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, key);
	}
	
	private Image getImageAndCache(ImageDescriptor descriptor) {
		final Image image = descriptor.createImage();
		imageMap.put(descriptor, image);
		return image;
	}
	
	public void dispose() {
		synchronized (imageMap) {
			for(Image image: imageMap.values())
				image.dispose();
		}
		imageMap.clear();
		synchronized (disabledMap) {
			for(Image image: disabledMap.values())
				image.dispose();
		}
		disabledMap.clear();
	}

}
