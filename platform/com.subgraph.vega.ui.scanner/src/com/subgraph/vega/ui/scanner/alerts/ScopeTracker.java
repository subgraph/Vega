package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.scope.ActiveScopeChangedEvent;
import com.subgraph.vega.api.model.scope.ITargetScope;

public class ScopeTracker {
	
	private final StructuredViewer viewer;
	private final IEventHandler scopeChangeListener;
	
	private IWorkspace currentWorkspace;
	private ViewerFilter activeScopeFilter;
	private boolean filterEnabled;
	
	ScopeTracker(StructuredViewer viewer) {
		this.viewer = viewer;
		this.scopeChangeListener = createScopeChangeListener();
	}
	
	private IEventHandler createScopeChangeListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ActiveScopeChangedEvent) {
					changeActiveScope(((ActiveScopeChangedEvent)event).getActiveScope());
				}
			}
		};
	}
	
	private void changeActiveScope(ITargetScope activeScope) {
		if(currentWorkspace == null) {
			return;
		}
		if(activeScopeFilter != null) {
			viewer.removeFilter(activeScopeFilter);
			activeScopeFilter = null;
		}
		if(filterEnabled) {
			activeScopeFilter = new CurrentScopeFilter(currentWorkspace, activeScope);
			viewer.addFilter(activeScopeFilter);
		}
	}
	
	public void workspaceChanged(IWorkspace workspace) {
		if(currentWorkspace != null) {
			currentWorkspace.getTargetScopeManager().removeActiveScopeChangeListener(scopeChangeListener);
		}
		currentWorkspace = workspace;
		if(workspace != null) {
			final ITargetScope activeScope = workspace.getTargetScopeManager().addActiveScopeChangeListener(scopeChangeListener);
			changeActiveScope(activeScope);
		}
	}
	
	public void setFilterByScopeEnabled(boolean value) {
		filterEnabled = value;
		final ITargetScope activeScope = currentWorkspace.getTargetScopeManager().getActiveScope();
		changeActiveScope(activeScope);
	}
}
