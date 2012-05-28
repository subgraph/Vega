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
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

public class HttpMessageDocument implements IDocumentListener {
	
	static final String SECTION_POSITION_CATEGORY = "__section_category";
	
	private final IDocument document;
	private final HttpMessageEntity messageEntity;
	private final Position headerSectionPosition;
	private final Position bodySectionPosition;
	private final HeaderDecoder headerDecoder;
	private boolean isHeaderSectionDirty;
	private boolean isBodySectionDirty;
	
	HttpMessageDocument(IDocument document, HttpMessageEntity entity) {
		this.document = document;
		this.messageEntity = entity;
		final Position[] sections = getSectionPositions();
		this.headerSectionPosition = sections[0];
		this.bodySectionPosition = sections[1];
		this.headerDecoder = new HeaderDecoder(this);
		
		document.addDocumentListener(this);
	}

	private Position[] getSectionPositions() {
		try {
			final Position[] ps = document.getPositions(SECTION_POSITION_CATEGORY);
			if(ps.length == 0 || ps.length > 2) {
				throw new IllegalStateException();
			} else if(ps.length == 1) {
				return new Position[] { ps[0], null };
			} else {
				return new Position[] { ps[0], ps[1] };
			}
		} catch (BadPositionCategoryException e) {
			throw new IllegalStateException(e);
		}
	}
	
	boolean isHeaderSectionDirty() {
		return isHeaderSectionDirty;
	}
	
	boolean isBodyEntityDirty() {
		if(bodySectionPosition != null) {
			return isBodySectionDirty;
		} else {
			return false;
		}
	}

	IDocument getDocument() {
		return document;
	}
	
	
	HttpMessageEntity getMessageEntity() {
		return messageEntity;
	}

	Position getHeaderSection() {
		return headerSectionPosition;
	}
	
	Position getBodySection() {
		return bodySectionPosition;
	}
	
	public String getHeaderSectionText() {
		return headerDecoder.getUndecodedHeaderContent();
	}

	public void setHeaderSectionText(String newText) {
		try {
			document.replace(headerSectionPosition.getOffset(), headerSectionPosition.getLength(), newText);
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getBodySectionText() {
		return getTextByPosition(bodySectionPosition);
	}

	private String getTextByPosition(Position p) {
		if(p == null) {
			return "";
		}
		
		try {
			return document.get(p.getOffset(), p.getLength());
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
	}

	void setHeaderDecodeState(boolean state) {
		if(state) {
			headerDecoder.decodeHeaders();
		} else {
			headerDecoder.undecodeHeaders();
		}
	}
	
	
	void toggleHeaderDecodeState() {
		headerDecoder.toggleDecodeState();
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		if(headerSectionPosition.overlapsWith(event.getOffset(), event.getLength())) {
			isHeaderSectionDirty = true;
		}
		
		if(bodySectionPosition != null && bodySectionPosition.overlapsWith(event.getOffset(), event.getLength())) {
			isBodySectionDirty = true;
		}
	}
	
	public void addProjectionAnnotations(ProjectionAnnotationModel model) {
		
		if(headerSectionPosition != null) {
			model.addAnnotation(new ProjectionAnnotation(), new Position(headerSectionPosition.offset, headerSectionPosition.length));
		}
		if(bodySectionPosition != null) {
			model.addAnnotation(new ProjectionAnnotation(), new Position(bodySectionPosition.offset, bodySectionPosition.length));
		}
	}
}
