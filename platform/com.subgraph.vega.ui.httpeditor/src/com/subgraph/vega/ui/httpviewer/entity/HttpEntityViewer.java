package com.subgraph.vega.ui.httpviewer.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

public class HttpEntityViewer extends Composite {	
	private enum EntityDisplayMode { DISPLAY_EMPTY, DISPLAY_TEXT, DISPLAY_IMAGE, DISPLAY_BINARY };
	
	private final StackLayout stack;
	private final HttpEntityImageViewer imageViewer;
	private final HttpEntityBinaryViewer binaryViewer;
	private final HttpEntityTextViewer textViewer;
	private final Composite emptyViewer;

	private boolean displayImages = true;
	private boolean displayImagesAsHex = false;
	private byte[] currentImageBytes;
	private ImageData currentImageData;
	
	private EntityDisplayMode currentDisplayMode;
	private HttpEntity currentlyDisplayedEntity;
	
	public HttpEntityViewer(Composite parent) {
		super(parent, SWT.NONE);
		stack = new StackLayout();
		setLayout(stack);
		textViewer = new HttpEntityTextViewer(this);
		imageViewer = new HttpEntityImageViewer(this);
		binaryViewer = new HttpEntityBinaryViewer(this);
		emptyViewer = createEmptyViewer(this);
		displayEmptyViewer();
	}

	public void displayHttpEntity(HttpEntity entity) {

		currentlyDisplayedEntity = entity;
		
		if(entity == null || entity.getContentLength() == 0) {
			displayEmptyViewer();
			return;
		}
		try {
			final String asString = EntityUtils.toString(entity);
			if(isBodyAscii(asString)) {
				displayTextViewer(asString, contentType(entity));
				return;
			}
			byte[] binary = EntityUtils.toByteArray(entity);
			if(binary == null || binary.length == 0) {
				displayEmptyViewer();
				return;
			}
			final ImageData imageData = binaryToImageData(binary);
			if(imageData != null) {
				displayImageViewer(imageData, binary);
			} else {
				displayHexViewer(binary);
			}
		} catch (ParseException e) {
			displayEmptyViewer();
		} catch (IOException e) {
			displayEmptyViewer();
		}
	}

	public HttpEntity getEntityContent() {
		if(currentDisplayMode == EntityDisplayMode.DISPLAY_TEXT && textViewer.isContentDirty()) {
			return textViewer.getEntityContent();	
		} else if ((currentDisplayMode == EntityDisplayMode.DISPLAY_IMAGE || currentDisplayMode == EntityDisplayMode.DISPLAY_BINARY) 
				&& binaryViewer.isContentDirty()) {
			return binaryViewer.getEntityContent();
		} else {
			return currentlyDisplayedEntity;
		}
	}

	public void setDisplayImages(boolean flag) {
		if(flag != displayImages) {
			displayImages = flag;
			if(currentImageBytes != null && currentImageData != null) {
				displayImageViewerByFlags();
			}
		}
	}
	
	public void setDisplayImagesAsHex(boolean flag) {
		if(flag != displayImagesAsHex) {
			displayImagesAsHex = flag;
			if(currentImageBytes != null && currentImageData != null) {
				displayImageViewerByFlags();
			}
		}
	}

	private ImageData binaryToImageData(byte[] binary) {
		try {
			return new ImageData(new ByteArrayInputStream(binary));
		} catch (SWTException e) {
			return null;
		}
	}

	private String contentType(HttpEntity entity) {
		final Header hdr = entity.getContentType();
		if(hdr == null)
			return "";
		else
			return hdr.getValue();
	}

	private boolean isBodyAscii(String body) {
		if(body == null || body.isEmpty())
			return false;
		
		final int total = (body.length() > 500) ? (500) : (body.length());
		int printable = 0;
		for(int i = 0; i < total; i++) {
			char c = body.charAt(i);
			if((c >= 0x20 && c <= 0x7F) || Character.isWhitespace(c))
				printable += 1;
		}
		return ((printable * 100) / total > 90);
	}

	private Composite createEmptyViewer(Composite parent) {
		final Composite viewer = new Composite(this, SWT.NONE);
		viewer.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return viewer;
	}
	
	private void displayEmptyViewer() {
		currentDisplayMode = EntityDisplayMode.DISPLAY_EMPTY;
		resetViewers();
		stack.topControl = emptyViewer;
		layout();
	}
	
	private void displayTextViewer(String text, String contentType) {
		currentDisplayMode = EntityDisplayMode.DISPLAY_TEXT;
		resetViewers();
		stack.topControl = textViewer;
		layout();
		textViewer.setInput(text, contentType);
	}
	
	private void displayImageViewer(ImageData imageData, byte[] imageBytes) {
		currentDisplayMode = EntityDisplayMode.DISPLAY_IMAGE;
		resetViewers();
		currentImageData = imageData;
		currentImageBytes = imageBytes;
		displayImageViewerByFlags();
	}
	
	private void displayImageViewerByFlags() {
		if(!displayImages) {
			stack.topControl = emptyViewer;
			layout();
		} else if(displayImagesAsHex) {
			stack.topControl = binaryViewer;
			layout();
			binaryViewer.setInput(currentImageBytes);
		} else {
			stack.topControl = imageViewer;
			layout();
			imageViewer.setImage(currentImageData);
		}
	}
	
	private void displayHexViewer(byte[] data) {
		currentDisplayMode = EntityDisplayMode.DISPLAY_BINARY;
		resetViewers();
		stack.topControl = binaryViewer;
		layout();
		binaryViewer.setInput(data);
	}
	
	private void resetViewers() {
		textViewer.clear();
		imageViewer.clear();
		binaryViewer.clear();
		currentImageBytes = null;
		currentImageData = null;
	}
}
