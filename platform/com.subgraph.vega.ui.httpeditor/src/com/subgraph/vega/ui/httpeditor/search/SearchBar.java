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
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.ui.httpeditor.Colors;

public class SearchBar extends Composite implements ModifyListener, KeyListener, VerifyKeyListener, ITextInputListener {
	
	public static void create(Composite parent, ProjectionViewer viewer, Colors colors) {
		new SearchBar(parent, viewer, colors);
		parent.setLayout(new SearchBarLayout(viewer.getTextWidget()));
	}

	private static final int KEY_ESC = 27;
	private static final int KEY_F = 102;
	
	private final static String ANNOTATION_MATCH = "httpeditor.search_match";
	
	private final ProjectionViewer viewer;
	private final Colors colors;
	private final Text searchText;
	private final Color searchTextBackground;
	private final Button previousMatch;
	private final Button nextMatch;
	
	private SearchResult currentSearch;
	private Annotation currentAnnotation;
	
	private SearchBar(Composite parent, ProjectionViewer viewer, Colors colors) {
		super(parent, SWT.NONE);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		configureViewer(viewer, colors);
		this.viewer = viewer;
		this.colors = colors;
		searchText = createSearchText();
		searchTextBackground = searchText.getBackground();
		nextMatch = createNextMatchButton();
		previousMatch = createPreviousMatchButton();
		pack();
		setVisible(false);
	}

	private void configureViewer(SourceViewer viewer, Colors colors) {
		attachHighlightPainter(viewer, colors);
		viewer.addTextInputListener(this);
		viewer.prependVerifyKeyListener(this);
	}

	private Text createSearchText() {
		final Text t = new Text(this, SWT.SINGLE | SWT.BORDER);
		t.setLayoutData(new RowData(150, SWT.DEFAULT));
		t.addModifyListener(this);
		t.addKeyListener(this);
		t.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleKeyEnter();
			}
		});
		return t;
	}
	
	private Button createNextMatchButton() {
		return createArrowButton(SWT.DOWN, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleNextMatch();
			}
		});
	}
	
	private Button createPreviousMatchButton() {
		return createArrowButton(SWT.UP, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handlePreviousMatch();
			}
		});
	}
	
	private Button createArrowButton(int flags, Listener listener) {
		final Button b = new Button(this, SWT.ARROW | flags);
		b.addListener(SWT.Selection, listener);
		b.addKeyListener(this);
		b.setEnabled(false);
		return b;
	}

	private void handleKeyEnter() {
		if(searchText.getText().isEmpty()) {
			return;
		} else if(currentSearch == null) {
			performSearch(searchText.getText());
		}else if(currentSearch.hasNext()) {
			handleNextMatch();
		} else if(currentSearch.getResultCount() > 1) {
			handleFirstMatch();
		}
	}
	
	private void handleFirstMatch() {
		if(currentSearch!= null && currentSearch.getResultCount() != 0) {
			setAnnotation(currentSearch.getFirstMatch());
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
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
		if(nextMatch.isDisposed() || previousMatch.isDisposed()) {
			return;
		}
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
			summarizeSearchResult();
			return;
		}
		performSearch(searchText.getText());
	}
	
	private void performSearch(String query) {
		clearCurrentSearch();
		final List<IRegion> matches = findAllMatches(query);
		currentSearch = new SearchResult(matches);
		handleFirstMatch();
	}

	private void setAnnotation(IRegion location) {
		clearAnnotation();
		viewer.getProjectionAnnotationModel().expandAll(location.getOffset(), location.getLength());
		final IAnnotationModel model = viewer.getAnnotationModel();
		if(model != null) {
			final Position pos = new Position(location.getOffset(), location.getLength());
			currentAnnotation = new Annotation(ANNOTATION_MATCH, false, "");
			model.addAnnotation(currentAnnotation, pos);
			revealDocumentRegion(location);
		}
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
			final IDocument document = viewer.getDocument();
			if(document != null) {
				final int documentLine = document.getLineOfOffset(offset);
				return viewer.modelLine2WidgetLine(documentLine);
			} else {
				return 0;
			}
		} catch (BadLocationException e) {
			throw new RuntimeException("Bad location calculating line of search result", e);
		}
	}
	
	private void clearAnnotation() {
		if(currentAnnotation != null) {
			final IAnnotationModel model = viewer.getAnnotationModel();
			if(model != null) {
				model.removeAnnotation(currentAnnotation);
			}
		}
		currentAnnotation = null;
	}
	
	private List<IRegion> findAllMatches(String query) {
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
		searchText.setToolTipText("("+ (currentSearch.getCurrentIndex() + 1) + " of " + 
		currentSearch.getResultCount() + ") results");
	}
	
	private void clearResultSummary() {
		if(!searchText.isDisposed()) {
			searchText.setToolTipText(null);
			searchText.setBackground(searchTextBackground);
		}
	}

	private void clearCurrentSearch() {
		clearAnnotation();
		clearResultSummary();
		currentSearch = null;
		enableButtonsForSearchState();
	}
	
	private void attachHighlightPainter(SourceViewer viewer, Colors colors) {
		final AnnotationPainter painter = new AnnotationPainter(viewer, createAnnotationAccess());
		painter.addHighlightAnnotationType(ANNOTATION_MATCH);
		painter.setAnnotationTypeColor(ANNOTATION_MATCH, colors.get(Colors.HIGHLIGHT_SEARCH_MATCH));
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

	@Override
	public void inputDocumentAboutToBeChanged(IDocument oldInput,
			IDocument newInput) {
		clearCurrentSearch();
	}

	@Override
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
	}

	private boolean isEventCtrlF(KeyEvent ev) {
		return ((ev.stateMask & SWT.CONTROL) != 0) && ev.keyCode == KEY_F;
	}
	
	private boolean isEventEscape(KeyEvent ev) {
		return ev.keyCode == KEY_ESC;
	}
	
	private void toggleSearchBar() {
		if(isVisible()) {
			hideSearchBar();
		} else {
			displaySearchBar();
		}
	}

	private void hideSearchBar() {
		clearCurrentSearch();
		setVisible(false);
	}
	
	private void displaySearchBar() {
		setVisible(true);
		getParent().layout();
		setFocus();
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if(isEventCtrlF(event)) {
			toggleSearchBar();
		}
		if(isEventEscape(event)) {
			hideSearchBar();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(isEventCtrlF(e) || isEventEscape(e)) {
			hideSearchBar();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
