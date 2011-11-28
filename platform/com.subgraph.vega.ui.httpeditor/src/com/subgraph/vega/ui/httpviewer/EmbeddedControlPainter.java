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
package com.subgraph.vega.ui.httpviewer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

public class EmbeddedControlPainter implements IPainter, PaintListener, ControlListener, VerifyListener, ITextInputListener {
	/* zero width no-break space */
	private final static char TAG_CHARACTER = '\uFEFF';
	private final static int ARBITRARY_TAG_CHARACTER_WIDTH = 10;
	private final static int HORIZONTAL_MARGIN = 2;
	private final static int VERTICAL_MARGIN = 8;
	private final TextViewer textViewer;
	private final StyledText textWidget;
	private final Control embeddedControl;
	private final int minControlHeight;
	private final Position controlPosition;
	private IPaintPositionManager paintPositionManager;
	private boolean isActive = false;
	
	public EmbeddedControlPainter(TextViewer textViewer, Control embeddedControl, int minControlHeight) {
		this.textViewer = textViewer;
		this.textWidget = textViewer.getTextWidget();
		this.embeddedControl = embeddedControl;
		this.minControlHeight = minControlHeight;
		this.controlPosition = new Position(0, 0);
		appendTagToDocument(textViewer.getDocument());
		textViewer.addTextInputListener(this);
		textWidget.addVerifyListener(this);
	}

	/**
	 * Extract the text from a document as a String, stripping off the embedded control
	 * tag if present.
	 * 
	 * @param document The document to extract the content from.
	 * @return The document content as a String, not including the tag string added by this class.
	 */
	static String getDocumentContent(IDocument document) {
		final String content = document.get();
		final String tag = "\n" + TAG_CHARACTER;
		final int tagIndex = content.indexOf(tag);
		if(tagIndex != -1) {
			return content.substring(0, tagIndex);
		} else {
			return content;
		}
	}

	private void appendTagToDocument(IDocument document) {
		if(document == null) {
			return;
		}
		final int length = document.getLength();
		final String append = "\n" + TAG_CHARACTER;
		try {
			document.replace(length, 0, append);
			styleTagCharacter(length + 1);
		} catch (BadLocationException e) {
			// Checked exception?  nonsense.
			throw new IllegalStateException("Bad location?", e);
		}
	}
	
	private void styleTagCharacter(int offset) {
		final IRegion region = textViewer.modelRange2WidgetRange(new Region(offset, 1));
		if(region != null) {
			final StyleRange style = new StyleRange();
			style.start = region.getOffset();
			style.length = region.getLength();
			style.metrics = new GlyphMetrics(minControlHeight, (2 * VERTICAL_MARGIN), ARBITRARY_TAG_CHARACTER_WIDTH);
			textWidget.setStyleRange(style);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public void paint(int reason) {
		final IDocument document = textViewer.getDocument();
		if(document == null) {
			deactivate(false);
			return;
		}
		
		if(!isActive) {
			textViewer.getTextWidget().addPaintListener(this);
			textViewer.getTextWidget().addControlListener(this);
			paintPositionManager.managePosition(controlPosition);
			isActive = true;
			findControlTag();
			return;
		}
		
		if(reason == TEXT_CHANGE || reason == KEY_STROKE || reason == INTERNAL || reason == CONFIGURATION)
			findControlTag();
	}
	
	private boolean findControlTag() {
		final StyledTextContent content = textWidget.getContent();
		final int count = content.getCharCount();
		final String text = content.getTextRange(0, count);
		final int idx = text.indexOf(TAG_CHARACTER);
		if(idx == -1) {
			controlPosition.setOffset(0);
			controlPosition.setLength(0);
			return false;
		}
		controlPosition.setOffset(idx);
		controlPosition.setLength(1);
		controlPosition.isDeleted = false;
		return true;
	}

	@Override
	public void deactivate(boolean redraw) {
		if(isActive) {
			isActive = false;
			textViewer.getTextWidget().removePaintListener(this);
			textViewer.getTextWidget().removeControlListener(this);
			if(paintPositionManager != null)
				paintPositionManager.unmanagePosition(controlPosition);
			if(redraw)
				redrawControlPosition();
		}
	}

	@Override
	public void setPositionManager(IPaintPositionManager manager) {
		this.paintPositionManager = manager;		
	}

	@Override
	public void paintControl(PaintEvent e) {
		if(textWidget != null)
			drawControl();
	}

	private int getWidgetOffsetForControl() {
		if(controlPosition.isDeleted || controlPosition.getLength() < 1)
			return -1;
		final int controlOffset = controlPosition.getOffset();
		final IRegion visibleRegion = textViewer.getVisibleRegion();
		if(visibleRegion.getOffset() > controlOffset || visibleRegion.getOffset() + visibleRegion.getLength() < controlOffset)
			return -1;
		return controlOffset - visibleRegion.getOffset();
	}

	private void drawControl() {
		final int offset = getWidgetOffsetForControl();
		if(offset > 0) {
			final Point location = textWidget.getLocationAtOffset(offset);
			Rectangle r = textWidget.getClientArea();
			int height = Math.max(minControlHeight, r.height - location.y - (VERTICAL_MARGIN * 2));
			embeddedControl.setBounds(
					location.x + HORIZONTAL_MARGIN, 
					location.y + VERTICAL_MARGIN, 
					r.width - (HORIZONTAL_MARGIN * 2), 
					height);
		}
	}
	
	private void redrawControlPosition() {
		final int offset = getWidgetOffsetForControl();
		final int length = controlPosition.getLength();
		if(offset > 0 && length > 0) {
			textWidget.redrawRange(offset, length, true);
		}
	}

	@Override
	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {		
	}

	@Override
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		appendTagToDocument(newInput);
		drawControl();
	}

	@Override
	public void controlMoved(ControlEvent e) {
	}

	@Override
	public void controlResized(ControlEvent e) {
		if(textWidget != null)
			drawControl();		
	}

	@Override
	public void verifyText(VerifyEvent e) {
		final int count = textWidget.getCharCount();
		/* Don't allow any changes at the end of the document */
		if(count - e.end < 4) {
			e.doit = false;
			return;
		}
		final int length = e.end - e.start;
		if(length > 0) {
			/* Probably redundant, but definitely don't allow removing tag character */
			final String oldText = (textWidget.getText(e.start, e.end - 1));
			if(oldText.indexOf(TAG_CHARACTER) != -1) {
				e.doit = false;
			}
		}
	}
}
