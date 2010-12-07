package com.subgraph.vega.api.model;

import com.subgraph.vega.api.events.IEvent;

public class WorkspaceResetEvent implements IEvent {
private final IWorkspace workspace;
	
	public WorkspaceResetEvent(IWorkspace workspace) {
		this.workspace = workspace;
	}

	public IWorkspace getWorkspace() {
		return workspace;
	}
}
