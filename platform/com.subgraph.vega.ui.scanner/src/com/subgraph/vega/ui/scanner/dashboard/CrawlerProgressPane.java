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
package com.subgraph.vega.ui.scanner.dashboard;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

public class CrawlerProgressPane extends Composite {

	private final StackLayout stack;
	private final Composite progressBarPage;
	private final Composite progressLabelPage;
	private final Label progressLabel;
	private final ProgressBar progressBar;
	
	public CrawlerProgressPane(Composite parent, Color background) {
		super(parent, SWT.NONE);
		stack = new StackLayout();
		setLayout(stack);
		setBackground(background);
		progressBarPage = createPage(this, background);
		progressLabelPage = createPage(this, background);
		
		progressLabel = createProgressLabel(progressLabelPage);
		progressLabel.setBackground(background);
		progressBar = createProgressBar(progressBarPage);
		progressBar.setBackground(background);
		
		setLabelText("Scanner idle.");
	}
	
	private Composite createPage(Composite parent, Color background) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout());
		page.setBackground(background);
		return page;
	}
	
	private Label createProgressLabel(Composite parent) {
		final Label label = new Label(parent, SWT.CENTER);
		label.setFont(JFaceResources.getBannerFont());
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		gd.verticalIndent = 20;
		label.setLayoutData(gd);
		return label;
	}
	
	private ProgressBar createProgressBar(Composite parent) {
		final ProgressBar progress = new ProgressBar(parent, SWT.SMOOTH);
		progress.setMinimum(0);
		progress.setMaximum(100);
		final GridData gd = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
		gd.widthHint = 400;
		progress.setLayoutData(gd);
		return progress;
	}

	void setLabelText(String text) {
		if(progressLabel.isDisposed()) {
			return;
		}
		progressLabel.setText(text);
		if(stack.topControl != progressLabelPage) {
			stack.topControl = progressLabelPage;
			layout();
		}
	}
	
	void setProgressBarValue(int value) {
		if(progressBar.isDisposed()) {
			return;
		}
		progressBar.setSelection(value);
		if(stack.topControl != progressBarPage) {
			stack.topControl = progressBarPage;
			layout();
		}
	}
}
