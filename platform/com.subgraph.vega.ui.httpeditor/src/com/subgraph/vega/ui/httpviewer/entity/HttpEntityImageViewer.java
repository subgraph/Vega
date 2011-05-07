package com.subgraph.vega.ui.httpviewer.entity;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class HttpEntityImageViewer extends Composite {

	private final Label imageLabel;
	public HttpEntityImageViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new RowLayout());
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		imageLabel = new Label(this, SWT.NONE);
	}
	
	void setImage(ImageData imageData) {
		clear();
		if(imageData != null) {
			final Image image = new Image(getDisplay(), imageData);
			imageLabel.setImage(image);
			imageLabel.pack();
		}
		layout();
	}
	
	void clear() {
		final Image lastImage = imageLabel.getImage();
		if(lastImage != null)
			lastImage.dispose();
		imageLabel.setImage(null);
	}
	
	@Override
	public void dispose() {
		clear();
		super.dispose();
	}
}
