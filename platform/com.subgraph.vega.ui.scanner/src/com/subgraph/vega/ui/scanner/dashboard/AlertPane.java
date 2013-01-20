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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlert.Severity;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

public class AlertPane extends Composite {
	private final static String ALERT_HIGH = "icons/alert_high.png";
	private final static String ALERT_MEDIUM = "icons/alert_medium.png";
	private final static String ALERT_LOW = "icons/alert_low.png";
	private final static String ALERT_INFO = "icons/alert_info.png";
	
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);		
	private final Map<Severity, AlertSeverityCell> alertSeverityCells = new HashMap<Severity, AlertSeverityCell>();
	private Composite rootComposite;
	
	AlertPane(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		setBackground(parent.getBackground());
		reset();
	}
	
	@Override
	public void dispose() {
		imageCache.dispose();
		super.dispose();
	}

	private void addSeverityCells(Color background) {
		for(Severity s: Severity.values()) {
			if(s.equals(Severity.UNKNOWN))
				continue;
			final AlertSeverityCell cell = createCellForSeverity(s, background);
			if(cell != null)
				alertSeverityCells.put(s, cell);
		}
	}
	
	private AlertSeverityCell createCellForSeverity(Severity s, Color background) {
		final String severityImageKey = getImageKeyForSeverity(s);
		final String severityLabel = getLabelForSeverity(s);
		if(severityImageKey == null || severityLabel == null)
			return null;
		final Image severityImage = imageCache.get(severityImageKey);
		final Image severityDisabledImage = imageCache.getDisabled(severityImageKey);
		if(severityImage == null || severityDisabledImage == null)
			return null;
		final AlertSeverityCell cell = new AlertSeverityCell(rootComposite, background, severityImage, severityDisabledImage, severityLabel);
		cell.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		cell.setBackground(background);
		return cell;
	}
	
	private String getImageKeyForSeverity(Severity s) {
		switch(s) {
		case HIGH:
			return ALERT_HIGH;
		case MEDIUM:
			return ALERT_MEDIUM;
		case LOW:
			return ALERT_LOW;
		case INFO:
			return ALERT_INFO;
		case UNKNOWN:
			return null;
		}
		return null;
	}
	
	private String getLabelForSeverity(Severity s) {
		switch(s) {
		case HIGH:
			return "High";
		case MEDIUM:
			return "Medium";
		case LOW:
			return "Low";
		case INFO:
			return "Info";
		case UNKNOWN:
			return "Unknown";
		}
		return null;
	}
	
	void reset() {
		if(rootComposite != null) {
			rootComposite.dispose();
		}
		rootComposite = new Composite(this, SWT.NULL);
		rootComposite.setBackground(getBackground());
		rootComposite.setLayout(new GridLayout());
		addSeverityCells(getParent().getBackground());
		layout();
	}

	void addAlert(final IScanAlert alert) {
		final AlertSeverityCell cell = alertSeverityCells.get(alert.getSeverity());
		if(cell == null)
			return;
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				cell.addAlert(alert);
			}
		});
	}
	
}
