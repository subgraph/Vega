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
package com.subgraph.vega.ui.httpeditor.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.ui.httpeditor.Colors;

class SearchBarWidget extends Composite implements ModifyListener {
	
	public final static String ANNOTATION_MATCH = "httpeditor.search_match";
	
	private final SearchBar searchBar;
	private final ProjectionViewer viewer;
	private final IDocument document;
	private final AnnotationModel annotationModel;
	private final Colors colors;
	private final Text searchText;
	private final Color searchTextBackground;
	private final Button previousMatch;
	private final Button nextMatch;
	
	private SearchResult currentSearch;
	private Annotation currentAnnotation;
	
	public SearchBarWidget(SearchBar searchBar, ProjectionViewer viewer, Colors colors, IDocument document, AnnotationModel model) {
		super(searchBar.getShell(), SWT.NONE);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		this.searchBar = searchBar;
		this.viewer = viewer;
		this.colors = colors;
		this.document = document;
		this.annotationModel = model;
		searchText = createSearchText();
		searchTextBackground = searchText.getBackground();
		nextMatch = createNextMatchButton();
		previousMatch = createPreviousMatchButton();
		pack();
	}

	private Text createSearchText() {
		final Text t = new Text(this, SWT.SINGLE | SWT.BORDER);
		t.setLayoutData(new RowData(150, SWT.DEFAULT));
		t.addModifyListener(this);
		t.addKeyListener(searchBar);
		return t;
	}
	
	private Button createNextMatchButton() {
		final Button b = new Button(this, SWT.ARROW | SWT.DOWN);
		b.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleNextMatch();
			}
		});
		return b;
	}
	
	private Button createPreviousMatchButton() {
		final Button b = new Button(this, SWT.ARROW | SWT.UP);
		b.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handlePreviousMatch();
			}
		});
		return b;
	}
	
	private void handleNextMatch() {
		if(currentSearch != null && currentSearch.hasNext()) {
			setAnnotation(currentSearch.getNextMatch());
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
	}
	
	private void handlePreviousMatch() {
		if(currentSearch != null && currentSearch.hasPrevious()) {
			setAnnotation(currentSearch.getPreviousMatch());
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
	}
	
	private void enableButtonsForSearchState() {
		if(currentSearch == null) {
			nextMatch.setEnabled(false);
			previousMatch.setEnabled(false);
			return;
		}
		nextMatch.setEnabled(currentSearch.hasNext());
		previousMatch.setEnabled(currentSearch.hasPrevious());
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if(searchText.getText().isEmpty()) {
			currentSearch = null;
			enableButtonsForSearchState();
			return;
		}
		performSearch(searchText.getText());
		
	}
	
	private void performSearch(String query) {
		final List<IRegion> matches = findAllMatches(query);
		currentSearch = new SearchResult(matches);
		if(currentSearch.getResultCount() > 0) {
			setAnnotation(currentSearch.getFirstMatch());
		} 
		enableButtonsForSearchState();
		summarizeSearchResult();
	}

	private void setAnnotation(IRegion location) {
		clearAnnotation();
		viewer.getProjectionAnnotationModel().expandAll(location.getOffset(), location.getLength());
		final Position pos = new Position(location.getOffset(), location.getLength());
		currentAnnotation = new Annotation(ANNOTATION_MATCH, false, "");
		annotationModel.addAnnotation(currentAnnotation, pos);
		revealDocumentRegion(location);
	}

	private void revealDocumentRegion(IRegion region) {
		final int startLine = getStartLineForMatch(region);
		final int endLine = getEndLineForMatch(region);
		if(!isLineVisible(startLine) || !isLineVisible(endLine)) {
			viewer.setTopIndex(startLine);
		}
	}
	
	private boolean isLineVisible(int line) {
		return viewer.getTopIndex() <= line && viewer.getBottomIndex() >= line; 
	}
	
	private int getStartLineForMatch(IRegion region) {
		return getLineForOffset(region.getOffset());
	}
	
	private int getEndLineForMatch(IRegion region) {
		return getLineForOffset(region.getOffset() + region.getLength() - 1);
	}
	
	private int getLineForOffset(int offset) {
		try {
			final int documentLine = document.getLineOfOffset(offset);
			return viewer.modelLine2WidgetLine(documentLine);
		} catch (BadLocationException e) {
			throw new RuntimeException("Bad location calculating line of search result", e);
		}
	}
	
	void clearAnnotation() {
		if(currentAnnotation != null) {
			annotationModel.removeAnnotation(currentAnnotation);
		}
		currentAnnotation = null;
	}
	
	private List<IRegion> findAllMatches(String query) {
		if(query.isEmpty()) {
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
				IRegion match = search.find(offset, query, true, false, false, false);
				if(match == null) {
					return results;
				}
				results.add(match);
				offset = match.getOffset() + match.getLength();
			} catch (BadLocationException e) {
				throw new RuntimeException("Bad location while performing search", e);
			}
		}
	}

	private void summarizeSearchResult() {
		if(currentSearch == null) {
			clearResultSummary();
			return;
		}

		if(currentSearch.getResultCount() == 0) {
			searchText.setToolTipText("No results found.");
			searchText.setBackground(colors.get(Colors.NO_SEARCH_RESULT_TEXT_BACKGROUND));
			return;
		}

		searchText.setBackground(searchTextBackground);
		searchText.setToolTipText("( "+ (currentSearch.getCurrentIndex() + 1) + " of " + 
		currentSearch.getResultCount() + ") results");
	}
	
	private void clearResultSummary() {
		searchText.setToolTipText(null);
		searchText.setBackground(searchTextBackground);
	}
}

