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

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.requestlogviewer.RequestLogViewer;
import com.subgraph.vega.ui.http.requestlogviewer.RequestResponseViewer;

/**
 * When multiple instances of this view are opened, the secondary view ID must be set to a unique value. The value is
 * used to differentiate between condition filter sets.
 */
public class HttpRequestView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.http";
	public final static String ID_PROXY_SECONDARY = "proxy";
	public final static String ID_PROXY = ID + ":" + ID_PROXY_SECONDARY; /** Compound ID identifying the non-closable base view in the proxy perspective */
	private RequestLogViewer requestLogViewer;
	private RequestResponseViewer requestResponseViewer;
	private IEventHandler workspaceListener;
	
	public HttpRequestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm form = new SashForm(parent, SWT.VERTICAL);

		requestLogViewer = new RequestLogViewer(form, getViewSite().getSecondaryId(), 0);

		requestResponseViewer = new RequestResponseViewer(form);
		requestLogViewer.setRequestResponseViewer(requestResponseViewer);

		form.setWeights(new int[] {40, 60});
		parent.pack();
		
		final ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
		final IModel model = Activator.getDefault().getModel();
		final ISelectionListener listener = new WebEntitySelectionListener(model, getViewSite().getSecondaryId());
		ss.addSelectionListener(listener);
		
		workspaceListener = new IEventHandler() {

			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceCloseEvent || event instanceof WorkspaceResetEvent) {
					handleWorkspaceCloseOrReset();
				}
			}
		};
		model.addWorkspaceListener(workspaceListener);
	}

	private void handleWorkspaceCloseOrReset() {
		final IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		resetToggleCommand("com.subgraph.vega.commands.proxyScan", false);
		resetToggleCommand("com.subgraph.vega.commands.proxyPassthrough", proxyService.isPassthrough());
	}
	
	private void resetToggleCommand(String commandId, boolean value) {
		final ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		final Command command = service.getCommand(commandId);
		if(command == null) {
			return;
		}
		final State state = command.getState("org.eclipse.ui.commands.toggleState");
		if(state != null) {
			state.setValue(value);
		}
	}
	
	public void focusOnRecord(long requestId) {
		requestLogViewer.focusOnRecord(requestId);
	}

	@Override
	public void setFocus() {
		requestLogViewer.setFocus();
	}
}
