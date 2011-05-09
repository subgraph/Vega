package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class AlertTreeLabelProvider extends LabelProvider {
	private final static String ALERT_ITEM = "icons/alert_item.png";

	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	public String getText(Object element) {
		if(element instanceof IAlertTreeNode) {
			final IAlertTreeNode node = (IAlertTreeNode) element;
			final int childCount = node.getAlertCount();
			if(childCount <= 1) {
				return node.getLabel();
			} else {
				return node.getLabel() + " ("+childCount + ")";
			}
		} else if(element instanceof IScanAlert) {
			return ((IScanAlert)element).getResource();
		} else {
			return "???";
		}
	}
	
	public Image getImage(Object element) {
		if(element instanceof IAlertTreeNode) {
			final String imageKey = ((IAlertTreeNode) element).getImage();
			if(imageKey != null) {
				return imageCache.get(imageKey);
			}
		} else if(element instanceof IScanAlert) {
			return imageCache.get(ALERT_ITEM);
		}
		return null;
	}
}
