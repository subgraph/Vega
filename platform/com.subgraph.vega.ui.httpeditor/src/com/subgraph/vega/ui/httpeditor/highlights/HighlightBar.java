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

import java.util.Collection;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import com.subgraph.vega.api.model.alerts.IScanAlertHighlight;
import com.subgraph.vega.ui.httpeditor.Colors;

public class HighlightBar extends Composite implements MatchChangeListener {
	private final static String ANNOTATION_HIGHLIGHT = "httpeditor.highlight";
	private final MatchHighlighter highlighter;
	private final Label alertLabel;
	private final NavigationButtons buttons;
	
	public HighlightBar(Composite parent, ProjectionViewer viewer, Colors colors) {
		super(parent, SWT.NONE);
		setLayout(createLayout());
		final Color c = colors.get(Colors.ALERT_HIGHLIGHT);
		setBackground(c);
		highlighter = new MatchHighlighter(viewer, c, ANNOTATION_HIGHLIGHT, false);
		alertLabel = createAlertLabel(c);
		buttons = new NavigationButtons(this, highlighter, this);
		buttons.setBackground(c);
		pack();
		hideHighlightBar();
	}

	private Layout createLayout() {
		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		return layout;
	}

	private Label createAlertLabel(Color color) {
		final Label label = new Label(this, SWT.SHADOW_IN | SWT.CENTER);
		label.setBackground(color);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		gd.widthHint = 150;
		label.setLayoutData(gd);
		return label;
	}
	
	public void clearHighlights() {
		highlighter.clearMatches();
		hideHighlightBar();
		alertLabel.setText("");
	}
	
	public void addAlertHighlights(Collection<IScanAlertHighlight> highlights) {
		for(IScanAlertHighlight h: highlights) {
			highlighter.searchMatches(h.getMatchString(), h.isRegularExpression(), h.isCaseSensitive());
		}
	}
	
	public void displayHighlights() {
		if(highlighter.isActive() && highlighter.getMatchCount() != 0) {
			highlighter.displayFirstMatch();
			if(highlighter.getMatchCount() > 1) {
				displayHighlightBar();
				buttons.enableButtonsForMatchState();
				summarizeMatchState();
			}
		}
	}
	
	private void summarizeMatchState() {
		if(!highlighter.isActive() || highlighter.getMatchCount() <= 1) {
			alertLabel.setText("");
			return;
		}
		
		final int current = highlighter.getCurrentIndex() + 1;
		final int count = highlighter.getMatchCount();
		alertLabel.setText(""+ current +" of "+ count +" highlights");
	}


	private void displayHighlightBar() {
		setVisible(true);
		pack();
		getParent().layout(true);
	}
	
	private void hideHighlightBar() {
		setVisible(false);
		getParent().layout(true);
	}

	@Override
	public void matchChanged() {
		summarizeMatchState();
	}
}
