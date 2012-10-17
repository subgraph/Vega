package com.subgraph.vega.internal.http.proxy;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.scope.ActiveScopeChangedEvent;
import com.subgraph.vega.api.model.scope.ITargetScope;

public class ProxyScannerScopeTracker {
	
	private final IEventHandler scopeChangeHandler;
	private final ProxyScanner proxyScanner;
	private IWorkspace currentWorkspace;
	private ITargetScope currentActiveScope;
	
	ProxyScannerScopeTracker(IModel model, ProxyScanner proxyScanner) {
		this.proxyScanner = proxyScanner;
		this.scopeChangeHandler = createScopeChangeListener();
		setCurrentWorkspace(model.addWorkspaceListener(createWorkspaceListener()));
	}
	
	ITargetScope getCurrentActiveScope() {
		return currentActiveScope;
	}

	private IEventHandler createWorkspaceListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent) {
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				} else if (event instanceof WorkspaceCloseEvent) {
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				} else if (event instanceof WorkspaceResetEvent) {
					handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			}
		};
	}
	
	private IEventHandler createScopeChangeListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ActiveScopeChangedEvent) {
					currentActiveScope = ((ActiveScopeChangedEvent)event).getActiveScope();
				}
			}
		};
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		setCurrentWorkspace(event.getWorkspace());
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		setCurrentWorkspace(event.getWorkspace());
		
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		setCurrentWorkspace(null);
	}
	
	private void setCurrentWorkspace(IWorkspace workspace) {
		final IWorkspace oldWorkspace = currentWorkspace;
		currentWorkspace = workspace;
		currentActiveScope = setScopeChangeListener(oldWorkspace, workspace);
		proxyScanner.handleWorkspaceChanged(workspace);
	}
	
	private ITargetScope setScopeChangeListener(IWorkspace oldWorkspace, IWorkspace newWorkspace) {
		if(oldWorkspace != null) {
			oldWorkspace.getTargetScopeManager().removeActiveScopeChangeListener(scopeChangeHandler);
		}
		if(newWorkspace != null) {
			return newWorkspace.getTargetScopeManager().addActiveScopeChangeListener(scopeChangeHandler);
		} else {
			return null;
		}
	}
}
