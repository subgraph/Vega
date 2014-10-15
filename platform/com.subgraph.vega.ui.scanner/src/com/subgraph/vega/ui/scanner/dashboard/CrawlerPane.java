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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.api.model.alerts.IScanInstance;

public class CrawlerPane extends Composite {
	private final CrawlerProgressPane progressPane;
	private final Label pathLabel;
	private final Label crawlLabel;
	private volatile boolean changed;
	private boolean isScannerPaused;
	private int scannerStatus;
	private String scannerPath;
	private int crawlerTotal;
	private int crawlerCompleted;
	private double crawlerPercent;

	CrawlerPane(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout());
		
		progressPane = new CrawlerProgressPane(this, parent.getBackground());
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true);
		gd.widthHint = 300;
		progressPane.setLayoutData(gd);
		
		pathLabel = new Label(this, SWT.CENTER);
		pathLabel.setFont(JFaceResources.getBannerFont());
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd.widthHint = 400;
		pathLabel.setLayoutData(gd);
		pathLabel.setBackground(parent.getBackground());
		
		crawlLabel = new Label(this, SWT.CENTER);
		crawlLabel.setFont(JFaceResources.getBannerFont());
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd.widthHint = 300;
		crawlLabel.setLayoutData(gd);
		crawlLabel.setBackground(parent.getBackground());
	}

	void setScannerPaused(boolean value) {
		isScannerPaused = value;
	}

	void renderChanges() {
		if(!changed || isDisposed())
			return;
		
		final Display display = getDisplay();
		if(display.isDisposed())
			return;
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				renderProgress();
				renderLabel();	
			}
		});
	}
	
	synchronized void updateCrawlerProgress(int status, String currentPath, int total, int completed) {
		if(status == scannerStatus && scannerPath == currentPath && total == crawlerTotal && completed == crawlerCompleted ) {
			return;
		}
		
		scannerStatus = status;
		scannerPath = currentPath;
		crawlerTotal = total;
		crawlerCompleted = completed;
		if(crawlerTotal == 0) {
			crawlerPercent = 0.0;
		} else {
			crawlerPercent = ((double)crawlerCompleted) / ((double)crawlerTotal) * 100.0;
		}
		changed = true;	
	}
	
	private void renderProgress() {
		switch(scannerStatus) {
		case IScanInstance.SCAN_CONFIG:
		case IScanInstance.SCAN_PROBING:
			progressPane.setLabelText("Probing server..");
		case IScanInstance.SCAN_STARTING:
		case IScanInstance.SCAN_AUDITING:
			progressPane.setProgressBarValue((int) crawlerPercent);
			break;
		case IScanInstance.SCAN_CANCELLED:
			progressPane.setLabelText("Scanner canceled.");
			break;
		case IScanInstance.SCAN_COMPLETED:
			progressPane.setLabelText("Scanner completed.");
			break;
			
		}
	}
	
	private void renderLabel() {
		if(crawlLabel.isDisposed()) {
			return;
		}
		if(crawlerPercent < 0.01) {
			crawlLabel.setText("");
		}

		if(scannerPath != null) {
			pathLabel.setText(scannerPath);
		}
		StringBuilder sb = new StringBuilder();
		sb.append(crawlerCompleted);
		sb.append(" out of ");
		sb.append(crawlerTotal);
		sb.append(" scanned (");
		if(isScannerPaused) {
			sb.append("Scanner Paused");
		} else {
			sb.append(String.format("%.1f%%", crawlerPercent));
		}
		sb.append(")");
		crawlLabel.setText(sb.toString());
	}
	
	
}
