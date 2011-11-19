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
package com.subgraph.vega.ui.scanner;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.subgraph.vega.ui.scanner.alerts.ScanAlertView;
import com.subgraph.vega.ui.scanner.info.ScanInfoView;

public class ScannerPerspectiveFactory implements IPerspectiveFactory {
	private final static String WEBSITE_VIEW = "com.subgraph.vega.views.website";
	public final static String HTTP_VIEW_SECONDARY_ID = "scanner"; /** Secondary ID for the scanner's com.subgraph.vega.views.http */

	@Override
	public void createInitialLayout(IPageLayout layout) {
		final IFolderLayout folder = layout.createFolder("main", IPageLayout.TOP, 0, layout.getEditorArea());
		folder.addView(ScanInfoView.ID);
		folder.addPlaceholder("*:*");
		layout.addStandaloneView(WEBSITE_VIEW, true, IPageLayout.LEFT, 0.25f, ScanInfoView.ID);
		layout.addStandaloneView(ScanAlertView.ID, true, IPageLayout.BOTTOM, 0.40f, WEBSITE_VIEW);
		layout.setEditorAreaVisible(false);
		layout.getViewLayout(ScanInfoView.ID).setCloseable(false);
		layout.getViewLayout(WEBSITE_VIEW).setCloseable(false);
		layout.getViewLayout(ScanAlertView.ID).setCloseable(false);
	}
}
