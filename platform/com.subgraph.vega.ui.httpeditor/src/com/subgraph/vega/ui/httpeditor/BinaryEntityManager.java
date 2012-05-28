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
package com.subgraph.vega.ui.httpeditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.ui.hexeditor.HexEditControl;
import com.subgraph.vega.ui.httpeditor.annotations.AnnotationPainter;
import com.subgraph.vega.ui.httpeditor.annotations.ImageAnnotation;

public class BinaryEntityManager {
	
	private final AnnotationPainter annotationPainter;
	private final SourceViewer viewer;
	private final SashForm sashForm;
	private final Composite root;
	private final HexEditControl hexEditor;
	
	private HttpMessageDocument activeDocument;
	private ImageAnnotation imageAnnotation;
	
	private boolean displayImages = true;
	private boolean displayImagesAsHex = false;
	
	BinaryEntityManager(SourceViewer viewer, SashForm sash, Composite root) {
		this.viewer = viewer;
		this.annotationPainter = new AnnotationPainter(viewer);
		this.sashForm = sash;
		this.root = root;
		this.hexEditor = new HexEditControl(sashForm);
		sashForm.setMaximizedControl(root);
	}
	
	void dispose() {
		annotationPainter.dispose();
	}
	void clear() {
		sashForm.setMaximizedControl(root);
		if(imageAnnotation != null) {
			removeImageAnnotation();
		}
		hexEditor.setInput(new byte[0]);
		activeDocument = null;
	}
	
	private boolean isImageEntity() {
		return activeDocument != null && activeDocument.getMessageEntity().isImageEntity();
	}
	
	boolean isContentDirty() {
		return hexEditor.isContentDirty();
	}
	
	byte[] getContent() {
		return hexEditor.getContent();
	}
	
	void displayImagesAsHex(boolean flag) {
		displayImagesAsHex = flag;
		displayImageForState();		
	}
	
	void displayImages(boolean flag) {
		displayImages = flag;
		displayImageForState();
	}

	void displayImageForState() {
		if(!isImageEntity()) {
			sashForm.setMaximizedControl(root);
			return;
		}
		if(displayImages) {
			if(displayImagesAsHex) {
				if(imageAnnotation != null) {
					removeImageAnnotation();
				}
				sashForm.setMaximizedControl(null);
			} else {
				if(isImageEntity())
					addImageAnnotation(activeDocument.getDocument(), activeDocument.getMessageEntity().getImageData());
				sashForm.setMaximizedControl(root);
			}
		} else {
			sashForm.setMaximizedControl(root);
			removeImageAnnotation();
		}
	}
	
	void displayNewDocument(HttpMessageDocument document) {
		
		clear();
		activeDocument = document;
		final HttpMessageEntity entity = document.getMessageEntity();
		
		if(!entity.isBinaryEntity() && !entity.isImageEntity()) {
			return;
		}
		
		if(entity.isImageEntity()) {
			hexEditor.setInput(entity.getBinaryData());
			displayImageForState();
		} else {
			hexEditor.setInput(entity.getBinaryData());
			sashForm.setMaximizedControl(null);
		}
		
	}
	
	private void addImageAnnotation(IDocument document, ImageData imageData) {
		if(imageAnnotation != null) {
			removeImageAnnotation();
		}
		final Image image = new Image(sashForm.getDisplay(), imageData);
		imageAnnotation = new ImageAnnotation(image);
		final Position p = padDocumentForImage(document, imageAnnotation.getPaddingString(viewer));
		annotationPainter.addAnnotation(imageAnnotation, p);
	}
	
	private void removeImageAnnotation() {
		if(imageAnnotation == null) {
			return;
		}
		final Position position = annotationPainter.getPosition(imageAnnotation);
		annotationPainter.removeAnnotation(imageAnnotation);
		activeDocument.getDocument().removePosition(position);
		try {
			activeDocument.getDocument().replace(position.offset, position.length, "");
		} catch (BadLocationException e) {
			throw new RuntimeException("Internal Error: "+ e.getMessage(), e);
		}
		
		imageAnnotation = null;
	}
	
	private Position padDocumentForImage(IDocument document, String padding) {
		final Position position = new Position(document.getLength(), padding.length());
		try {
			document.replace(document.getLength(), 0, padding);
			document.addPosition(position);
		} catch (BadLocationException e) {
			throw new RuntimeException("Internal Error: "+ e.getMessage(), e);
		}
		return position;
	}
}
