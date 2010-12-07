package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlert.Severity;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class ScanAlertLabelProvider extends LabelProvider {
	private final static String ALERT_LOW = "icons/alert_low.png";
	private final static String ALERT_MEDIUM = "icons/alert_medium.png";
	private final static String ALERT_HIGH = "icons/alert_high.png";
	private final static String ALERT_INFO = "icons/alert_info.png";
	private final static String ALERT_ITEM = "icons/alert_item.png";
	
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	
	public String getText(Object element) {
		if(element instanceof Severity) {
			Severity severity = (Severity) element;
			switch(severity) {
			case HIGH:
				return "High";
			case MEDIUM:
				return "Medium";
			case LOW:
				return "Low";
			case INFO:
				return "Info";
			}
		} else if(element instanceof IScanAlert) {
			IScanAlert alert = (IScanAlert) element;
			Object ob = alert.getProperty("resource");
			if(ob instanceof String && ob != null) {
				String resource = (String) ob;
				return alert.getTitle() + " ("+ resource + ")";
			}
			return alert.getTitle();
		} 
		return null;
	}
	
	public Image getImage(Object element) {
		if(element instanceof Severity) {
			Severity severity = (Severity) element;
			switch(severity) {
			case HIGH:
				return imageCache.get(ALERT_HIGH);
			case MEDIUM:
				return imageCache.get(ALERT_MEDIUM);
			case LOW:
				return imageCache.get(ALERT_LOW);
			case INFO:
				return imageCache.get(ALERT_INFO);
			}
		} else if(element instanceof IScanAlert) {
			return imageCache.get(ALERT_ITEM);
		}
		return null;
	}

}
