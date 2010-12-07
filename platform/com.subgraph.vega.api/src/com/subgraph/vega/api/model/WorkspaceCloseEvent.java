package com.subgraph.vega.api.model;

import com.subgraph.vega.api.events.IEvent;

public class WorkspaceCloseEvent implements IEvent {
	private final IWorkspace workspace;
	
	public WorkspaceCloseEvent(IWorkspace workspace) {
		this.workspace = workspace;
	}
	
	public IWorkspace getWorkspace() {
		return workspace;
	}
}
