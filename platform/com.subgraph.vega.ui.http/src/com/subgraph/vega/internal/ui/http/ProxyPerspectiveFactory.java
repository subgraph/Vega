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
package com.subgraph.vega.internal.ui.http;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.subgraph.vega.ui.http.identities.IdentitiesView;
import com.subgraph.vega.ui.http.intercept.InterceptView;
import com.subgraph.vega.ui.http.intercept.queue.InterceptQueueView;
import com.subgraph.vega.ui.http.request.view.HttpRequestView;

public class ProxyPerspectiveFactory implements IPerspectiveFactory {
	private final static String PROXY_FOLDER = "proxy";
	private final static String WEBSITE_VIEW = "com.subgraph.vega.views.website";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		IFolderLayout proxyFolder = layout.createFolder(PROXY_FOLDER, IPageLayout.TOP, 0f, layout.getEditorArea());
		proxyFolder.addView(HttpRequestView.ID_PROXY);
		proxyFolder.addView(InterceptView.ID);
		proxyFolder.addView(InterceptQueueView.ID);
		proxyFolder.addView(IdentitiesView.ID);
		layout.addView(WEBSITE_VIEW, IPageLayout.LEFT, 0.25f, PROXY_FOLDER);
		layout.setEditorAreaVisible(false);
		layout.getViewLayout(HttpRequestView.ID_PROXY).setCloseable(false);
		layout.getViewLayout(InterceptView.ID).setCloseable(false);
		layout.getViewLayout(InterceptQueueView.ID).setCloseable(false);
		layout.getViewLayout(WEBSITE_VIEW).setCloseable(false);
	}

}
