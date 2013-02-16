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
	private final static String SCAN_INFO_FOLDER = "scanInfo";
	private final static String MGMT_FOLDER = "connection";
	private final static String WEBSITE_VIEW = "com.subgraph.vega.views.website";
	public final static String HTTP_VIEW_SECONDARY_ID = "scanner"; /** Secondary ID for the scanner's com.subgraph.vega.views.http */

	@Override
	public void createInitialLayout(IPageLayout layout) {
		final IFolderLayout scanFolder = layout.createFolder(SCAN_INFO_FOLDER, IPageLayout.TOP, 0, layout.getEditorArea());
		scanFolder.addView(ScanInfoView.ID);
		layout.getViewLayout(ScanInfoView.ID).setCloseable(false);
		scanFolder.addPlaceholder("*:*");

		layout.addStandaloneView(WEBSITE_VIEW, true, IPageLayout.LEFT, 0.25f, ScanInfoView.ID);
		layout.getViewLayout(WEBSITE_VIEW).setCloseable(false);
		layout.addStandaloneView(ScanAlertView.ID, true, IPageLayout.BOTTOM, 0.40f, WEBSITE_VIEW);
		layout.getViewLayout(ScanAlertView.ID).setCloseable(false);

		final IFolderLayout mgmtFolder = layout.createFolder(MGMT_FOLDER, IPageLayout.BOTTOM, 0.75f, SCAN_INFO_FOLDER);
		mgmtFolder.addView("com.subgraph.vega.views.identity.identities");
		
		layout.setEditorAreaVisible(false);
	}
}
