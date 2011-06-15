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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.api.model.alerts.IScanAlert;

/**
 * Renders all the information for a particular alert severity including the
 * AlertItemRows corresponding to each alert type.  There are one of these
 * for each alert severity.
 */
public class AlertSeverityCell extends Composite {

	private final Label imageLabel;
	private final Label labelLabel;
	private final Label countLabel;
	private final Image image;
	private int totalCount = 0;
	
	private Map<String, AlertItemRow> alertTitleToItem = new HashMap<String, AlertItemRow>();
		
	AlertSeverityCell(Composite parent, Color background, Image image, Image disabled, String label) {
		super(parent, SWT.NONE);
		this.image = image;

		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 2;
		setLayout(layout);
		imageLabel = new Label(this, SWT.NONE);
		imageLabel.setImage(disabled);
		imageLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		imageLabel.setBackground(background);
		
		labelLabel = new Label(this, SWT.NONE);
		labelLabel.setText(label);
		labelLabel.setFont(JFaceResources.getBannerFont());
		setLabelForegroundGrey(labelLabel);

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelLabel.setLayoutData(gd);
		labelLabel.setBackground(background);
		
		countLabel = new Label(this, SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd.widthHint = 85;
		countLabel.setLayoutData(gd);
		countLabel.setText("(None found)");
		countLabel.setBackground(background);
		setLabelForegroundGrey(countLabel);
				
		final Composite spacer = new Composite(this, SWT.NONE);
		spacer.setBackground(background);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 5;
		gd.horizontalSpan = 3;
		spacer.setLayoutData(gd);
	}

	private void setLabelForegroundGrey(Label label) {
		if(!label.isDisposed()) {
			label.setData("saved-foreground", label.getForeground());
			label.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
		}
	}
	
	private void restoreLabelForeground(Label label) {
		final Object ob = label.getData("saved-foreground");
		if(ob instanceof Color) {
			if(!label.isDisposed()) {
				label.setForeground((Color)ob);
			}
		}
	}
	
	void addAlert(IScanAlert alert) {
		if(isDisposed()) {
			return;
		}
		incrementTotalCount();
		final String title = alert.getTitle();
		if(!alertTitleToItem.containsKey(title)) {
			alertTitleToItem.put(title, createAlertItemRow(title));
			getParent().layout();
		}
		AlertItemRow row = alertTitleToItem.get(title);
		row.incrementCount();
	}
	
	private AlertItemRow createAlertItemRow(String title) {
		AlertItemRow row = new AlertItemRow(this, title);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 3;
		gd.horizontalIndent = 15;
		row.setLayoutData(gd);
		return row;
	}
	
	private void incrementTotalCount() {
		if(totalCount == 0) {
			if(!imageLabel.isDisposed()) {
				imageLabel.setImage(image);
			}
			restoreLabelForeground(labelLabel);
			restoreLabelForeground(countLabel);
		}
		totalCount += 1;
		if(!countLabel.isDisposed()) {
			countLabel.setText("("+ Integer.toString(totalCount) +" found)");
		}
	}
}
