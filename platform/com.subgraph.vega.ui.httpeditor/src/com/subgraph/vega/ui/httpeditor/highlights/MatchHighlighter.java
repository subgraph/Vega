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
package com.subgraph.vega.ui.httpeditor.highlights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.graphics.Color;

public class MatchHighlighter implements ITextInputListener {
	
	private final ProjectionViewer viewer;
	private final boolean isDisplaySingleMatch;
	
	private final MatchingRegions matchingRegions;
	private RegionInfo currentRegion;
	private boolean areAnnotationsAdded;
	private boolean isActive;
	
	public MatchHighlighter(ProjectionViewer viewer, Color highlightColor, String annotationType, boolean displaySingle) {
		this.viewer = viewer;
		this.isDisplaySingleMatch = displaySingle;
		this.matchingRegions = new MatchingRegions(annotationType);
		configureViewer(viewer, highlightColor, annotationType);
	}

	public boolean isActive() {
		return isActive;
	}
	
	public int getMatchCount() {
		return matchingRegions.getMatchCount();
	}
	
	public int getCurrentIndex() {
		return matchingRegions.getCurrentIndex();
	}
	
	public void displayFirstMatch() {
		if(matchingRegions.getMatchCount() == 0) {
			return;
		}
		maybeDisplayAllAnnotations();
		maybeHideAnnotation();
		currentRegion = matchingRegions.getFirstRegion();
		maybeDisplayAnnotation();
		revealDocumentRegion(currentRegion.getRegion());
	}
	
	public void displayNextMatch() {
		if(!matchingRegions.hasNext()) {
			return;
		}
		maybeHideAnnotation();
		currentRegion = matchingRegions.getNextRegion();
		maybeDisplayAnnotation();
		revealDocumentRegion(currentRegion.getRegion());
	}
	
	public void displayPreviousMatch() {
		if(!matchingRegions.hasPrevious()) {
			return;
		}
		maybeHideAnnotation();
		currentRegion = matchingRegions.getPreviousRegion();
		maybeDisplayAnnotation();
		revealDocumentRegion(currentRegion.getRegion());
	}
	
	public void clearMatches() {
		for(RegionInfo regionInfo: matchingRegions.getAllRegions()) {
			regionInfo.removeHighlight(viewer);
		}
		matchingRegions.reset();
		currentRegion = null;
		areAnnotationsAdded = false;
		isActive = false;
	}
	
	public void searchMatches(String query, boolean isRegex, boolean isCaseSensitive) {
		matchingRegions.addRegions( findAllMatches(query, isRegex, isCaseSensitive) );
		isActive = true;
	}
	
	public boolean hasNextMatch() {
		return matchingRegions.hasNext();
	}
	
	public boolean hasPreviousMatch() {
		return matchingRegions.hasPrevious();
	}

	private void maybeDisplayAllAnnotations() {
		if(!isDisplaySingleMatch && !areAnnotationsAdded) {
			for(RegionInfo regionInfo: matchingRegions.getAllRegions()) {
				regionInfo.displayHighlight(viewer);
			}
			areAnnotationsAdded = true;
		}
	}
	
	private void maybeHideAnnotation() {
		if(isDisplaySingleMatch && currentRegion != null) {
			currentRegion.removeHighlight(viewer);
		}
	}
	
	private void maybeDisplayAnnotation() {
		if(isDisplaySingleMatch && currentRegion != null) {
			currentRegion.displayHighlight(viewer);
		}
	}
	
	private void revealDocumentRegion(IRegion r) {
		final int startLine = getStartLineForMatch(r);
		final int endLine = getEndLineForMatch(r);
		if(!isLineVisible(startLine) || !isLineVisible(endLine)) {
			viewer.setTopIndex(startLine);
		}
	}
	
	private boolean isLineVisible(int line) {
		return viewer.getTopIndex() <= line && viewer.getBottomIndex() >= line;
	}

	private int getEndLineForMatch(IRegion r) {
		return getLineForOffset(r.getOffset() + r.getLength() - 1);
	}

	private int getLineForOffset(int offset) {
		try {
			final IDocument document = viewer.getDocument();
			if(document != null) {
				final int documentLine = document.getLineOfOffset(offset);
				return viewer.modelLine2WidgetLine(documentLine);
			} else {
				return 0;
			}
		} catch (BadLocationException e) {
			throw new RuntimeException("Bad location calculating line of highlight");
		}
	}

	private int getStartLineForMatch(IRegion r) {
		return getLineForOffset(r.getOffset());
	}

	private void configureViewer(SourceViewer viewer, Color highlightColor, String annotationType) {
		viewer.addTextInputListener(this);
		attachHighlightPainter(viewer, highlightColor, annotationType);
	}
	
	private void attachHighlightPainter(SourceViewer viewer, Color highlightColor, String annotationType) {
		final AnnotationPainter painter = new AnnotationPainter(viewer, createAnnotationAccess());
		painter.addHighlightAnnotationType(annotationType);
		painter.setAnnotationTypeColor(annotationType, highlightColor);
		viewer.addPainter(painter);
		viewer.addTextPresentationListener(painter);
	}
	
	private IAnnotationAccess createAnnotationAccess() {
		return new IAnnotationAccess() {
			@Override
			public boolean isTemporary(Annotation annotation) {
				return true;
			}
			
			@Override
			public boolean isMultiLine(Annotation annotation) {
				return true;
			}
			
			@Override
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}
		};
	}

	private List<IRegion> findAllMatches(String query, boolean isRegex, boolean caseSensitive) {
		final IDocument document = viewer.getDocument();
		if(query.isEmpty() || document == null) {
			return Collections.emptyList();
		}
		final FindReplaceDocumentAdapter search = new FindReplaceDocumentAdapter(document);
		final ArrayList<IRegion> results = new ArrayList<IRegion>();
		int offset = 0;
		while(true) {
			if(offset >= document.getLength()) {
				return results;
			}
			try {
				IRegion match = search.find(offset, query, true, caseSensitive, false, isRegex);
				if(match == null) {
					return results;
				}
				results.add(match);
				offset = match.getOffset() + match.getLength();
			} catch (BadLocationException e) {
				throw new RuntimeException("Bad location while performing search");
			}
		}
	}

	@Override
	public void inputDocumentAboutToBeChanged(IDocument oldInput,
			IDocument newInput) {
		clearMatches();
	}

	@Override
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
	}
}
