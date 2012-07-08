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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
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
import com.subgraph.vega.ui.httpeditor.highlights.MatchHighlighter;

public class SearchBar extends Composite implements ModifyListener, KeyListener, VerifyKeyListener, ITextInputListener {
	
	public static void create(Composite parent, ProjectionViewer viewer, Colors colors) {
		new SearchBar(parent, viewer, colors);
		parent.setLayout(new SearchBarLayout(viewer.getTextWidget()));
	}

	private static final int KEY_ESC = 27;
	private static final int KEY_F = 102;
	
	private final static String ANNOTATION_MATCH = "httpeditor.search_match";
	
	private final MatchHighlighter highlighter;
	private final Color noResultBackground;
	private final Text searchText;
	private final Color searchTextBackground;
	private final Button previousMatch;
	private final Button nextMatch;
	
	private SearchBar(Composite parent, ProjectionViewer viewer, Colors colors) {
		super(parent, SWT.NONE);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		configureViewer(viewer);
		this.highlighter = new MatchHighlighter(viewer, colors.get(Colors.HIGHLIGHT_SEARCH_MATCH), ANNOTATION_MATCH, true);
		this.noResultBackground = colors.get(Colors.NO_SEARCH_RESULT_TEXT_BACKGROUND);
		searchText = createSearchText();
		searchTextBackground = searchText.getBackground();
		nextMatch = createNextMatchButton();
		previousMatch = createPreviousMatchButton();
		pack();
		setVisible(false);
	}


	private void configureViewer(SourceViewer viewer) {
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
		} else if (!highlighter.isActive()) {
			performSearch(searchText.getText());
		} else if (highlighter.hasNextMatch()) {
			handleNextMatch();
		} else if (highlighter.getMatchCount() > 1) {
			handleFirstMatch();
		}
	}
	
	private void handleFirstMatch() {
		if(highlighter.isActive() && highlighter.getMatchCount() != 0) {
			highlighter.displayFirstMatch();
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
	}

	private void handleNextMatch() {
		if(highlighter.isActive() && highlighter.hasNextMatch()) {
			highlighter.displayNextMatch();
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
	}
	
	private void handlePreviousMatch() {
		if(highlighter.isActive() && highlighter.hasPreviousMatch()) {
			highlighter.displayPreviousMatch();
		}
		enableButtonsForSearchState();
		summarizeSearchResult();
	}
	
	private void enableButtonsForSearchState() {
		if(nextMatch.isDisposed() || previousMatch.isDisposed()) {
			return;
		}
		if(!highlighter.isActive()) {
			nextMatch.setEnabled(false);
			previousMatch.setEnabled(false);
			return;
		}
		nextMatch.setEnabled(highlighter.hasNextMatch());
		previousMatch.setEnabled(highlighter.hasPreviousMatch());
	}

	@Override
	public void modifyText(ModifyEvent e) {
		if(searchText.getText().isEmpty()) {
			highlighter.clearMatches();
			enableButtonsForSearchState();
			summarizeSearchResult();
			return;
		}
		performSearch(searchText.getText());
	}
	
	private void performSearch(String query) {
		highlighter.clearMatches();
		highlighter.searchMatches(query, false);
		handleFirstMatch();
	}

	private void summarizeSearchResult() {
		if(!highlighter.isActive()) {
			clearResultSummary();
			return;
		}
		
		if(highlighter.getMatchCount() == 0) {
			searchText.setToolTipText("No results found.");
			searchText.setBackground(noResultBackground);
			return;
		}

		searchText.setBackground(searchTextBackground);
		searchText.setToolTipText("("+ (highlighter.getCurrentIndex() + 1) + " of " +
				highlighter.getMatchCount() + ") results");
	}
	
	private void clearResultSummary() {
		if(!searchText.isDisposed()) {
			searchText.setToolTipText(null);
			searchText.setBackground(searchTextBackground);
		}
	}

	private void clearCurrentSearch() {
		highlighter.clearMatches();
		clearResultSummary();
		enableButtonsForSearchState();
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
