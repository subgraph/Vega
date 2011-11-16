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
package com.subgraph.vega.ui.http.request.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.ui.http.requestlogviewer.RequestLogViewer;
import com.subgraph.vega.ui.http.requestlogviewer.RequestResponseViewer;

public class HttpRequestView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.http";
	private RequestLogViewer requestLogViewer;
	private RequestResponseViewer requestResponseViewer;

	public HttpRequestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm form = new SashForm(parent, SWT.VERTICAL);

		requestLogViewer = new RequestLogViewer(form);
		requestLogViewer.registerContextMenu(getSite());

		requestResponseViewer = new RequestResponseViewer(form);
		requestLogViewer.setRequestResponseViewer(requestResponseViewer);

		form.setWeights(new int[] {40, 60});
		parent.pack();
	}

	public void focusOnRecord(long requestId) {
		requestLogViewer.focusOnRecord(requestId);
	}

	@Override
	public void setFocus() {
		requestLogViewer.setFocus();
	}

}
