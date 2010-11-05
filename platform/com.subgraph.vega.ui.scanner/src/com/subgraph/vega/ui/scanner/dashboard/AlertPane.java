package com.subgraph.vega.ui.scanner.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlert.Severity;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class AlertPane extends Composite {
	private final static String ALERT_HIGH = "icons/alert_high.png";
	private final static String ALERT_MEDIUM = "icons/alert_medium.png";
	private final static String ALERT_LOW = "icons/alert_low.png";
	private final static String ALERT_INFO = "icons/alert_info.png";
	
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);		
	private final Map<Severity, AlertSeverityCell> alertSeverityCells = new HashMap<Severity, AlertSeverityCell>();
	
	AlertPane(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout());
		addSeverityCells(parent.getBackground());
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
		final AlertSeverityCell cell = new AlertSeverityCell(this, background, severityImage, severityDisabledImage, severityLabel);
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
		}
		return null;
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
