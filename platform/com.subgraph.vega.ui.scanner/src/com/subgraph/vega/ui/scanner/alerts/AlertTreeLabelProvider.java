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
package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.util.images.ImageCache;

public class AlertTreeLabelProvider extends LabelProvider {
	private final static String ALERT_ITEM = "icons/alert_item.png";
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private final AlertTreeContentProvider contentProvider;

	public AlertTreeLabelProvider(AlertTreeContentProvider contentProvider) {
		super();
		this.contentProvider = contentProvider;
	}
	
	@Override
	public void dispose() {
		imageCache.dispose();
		super.dispose();
	}

	@Override
	public String getText(Object element) {
		if(element instanceof IAlertTreeNode) {
			final IAlertTreeNode node = (IAlertTreeNode) element;
			final int childCount = node.getAlertCount();
			if(childCount <= 1) {
				return node.getLabel();
			} else {
				return node.getLabel() + " ("+ childCount +")";
			}
		} else if(element instanceof IScanAlert) {
			return ((IScanAlert)element).getResource();
		} else {
			return "???";
		}
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof AlertScanNode) {
			final AlertScanNode node = (AlertScanNode) element;
			if (node.getScanInstance().isActive()) {
				final boolean activeBlinkState = contentProvider.isBlinkStateActive();
				if(activeBlinkState) {
					return imageCache.get(node.getImage());
				} else {
					return imageCache.getDisabled(node.getImage());
				}
			} else {
				return imageCache.get(node.getImage());
			}
		} else if(element instanceof IAlertTreeNode) {
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
