package com.subgraph.vega.ui.scanner.alerts;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;

public class WorkspaceTracker implements IEventHandler {
	
	static WorkspaceTracker create(IModel model, ScanAlertView alertView, ScopeTracker scopeTracker) {
		final WorkspaceTracker tracker = new WorkspaceTracker(alertView, scopeTracker);
		final IWorkspace workspace = model.addWorkspaceListener(tracker);
		tracker.setCurrentWorkspace(workspace);
		return tracker;
	}
	
	private final ScanAlertView alertView;
	private final ScopeTracker scopeTracker;
	
	private WorkspaceTracker(ScanAlertView alertView, ScopeTracker scopeTracker) {
		this.alertView = alertView;
		this.scopeTracker = scopeTracker;
	}
	
	private void setCurrentWorkspace(IWorkspace workspace) {
		alertView.workspaceChanged(workspace);
		scopeTracker.workspaceChanged(workspace);
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof WorkspaceOpenEvent) {
			setCurrentWorkspace(((WorkspaceOpenEvent)event).getWorkspace());
		} else if(event instanceof WorkspaceCloseEvent) {
			setCurrentWorkspace(null);
		} else if(event instanceof WorkspaceResetEvent) {
			setCurrentWorkspace(null);
			setCurrentWorkspace( ((WorkspaceResetEvent)event).getWorkspace());
		}
	}
}
