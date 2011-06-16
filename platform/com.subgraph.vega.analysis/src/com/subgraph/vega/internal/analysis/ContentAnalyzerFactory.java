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
package com.subgraph.vega.internal.analysis;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanInstance;

public class ContentAnalyzerFactory implements IContentAnalyzerFactory {
	
	private final IEventHandler workspaceEventHandler;
	private IModel model;
	private IWorkspace currentWorkspace;
	
	
	public ContentAnalyzerFactory() {
		workspaceEventHandler = createWorkspaceEventHandler();
	}
	
	@Override
	public IContentAnalyzer createContentAnalyzer(IScanInstance scanInstance) {
		return new ContentAnalyzer(this, scanInstance);
	}

	private IEventHandler createWorkspaceEventHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent)
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceCloseEvent)
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				else if(event instanceof WorkspaceResetEvent)
					handleWorkspaceReset((WorkspaceResetEvent) event);
			}
		};
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		currentWorkspace = event.getWorkspace();
	}

	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		currentWorkspace = null;
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		currentWorkspace = event.getWorkspace();
	}

	IWorkspace getCurrentWorkspace() {
		return currentWorkspace;
	}

	void activate() {
		currentWorkspace = model.addWorkspaceListener(workspaceEventHandler);
	}
	
	void deactivate() {
		model.removeWorkspaceListener(workspaceEventHandler);
		currentWorkspace = null;
	}

	protected void setModel(IModel model) {
		this.model = model;
	}
	
	protected void unsetModel(IModel model) {
		this.model = null;
	}
}
