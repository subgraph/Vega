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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class NavigationButtons extends Composite {

	private final MatchHighlighter highlighter;
	private final MatchChangeListener listener;
	private final Button nextMatch;
	private final Button previousMatch;
	
	public NavigationButtons(Composite parent, MatchHighlighter highlighter, MatchChangeListener listener) {
		super(parent, SWT.NONE);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		this.highlighter = highlighter;
		this.listener = listener;
		this.nextMatch = createNextMatchButton();
		this.previousMatch = createPreviousMatchButton();
		pack();
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
		b.setEnabled(false);
		return b;
	}

	public void handleNextMatch() {
		if(highlighter.isActive() && highlighter.hasNextMatch()) {
			highlighter.displayNextMatch();
		}
		enableButtonsForMatchState();
		listener.matchChanged();
	}

	public void handlePreviousMatch() {
		if(highlighter.isActive() && highlighter.hasPreviousMatch()) {
			highlighter.displayPreviousMatch();
		}
		enableButtonsForMatchState();
		listener.matchChanged();
	}

	public void enableButtonsForMatchState() {
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
}
