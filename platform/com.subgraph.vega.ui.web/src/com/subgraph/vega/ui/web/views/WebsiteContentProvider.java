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
package com.subgraph.vega.ui.web.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.NewWebEntityEvent;
import com.subgraph.vega.api.model.web.UpdatedWebEntityEvent;
import com.subgraph.vega.ui.tree.web.WebModelAdapter;

public class WebsiteContentProvider implements ITreeContentProvider {
	private final Object[] NULL_OB = new Object[0];
	private IWorkspace workspace;
	private StructuredViewer viewer;
	private final IEventHandler modelListener = createModelListener();
	private final List<IWebHost> webHosts = new ArrayList<IWebHost>();
	private final WebModelAdapter treeAdapter = new WebModelAdapter();
	
	public Object[] getChildren(Object parentElement) {
		return treeAdapter.getChildren(parentElement);
	}
	
	public Object getParent(Object element) {
		return treeAdapter.getParent(element);
	}
	
	public boolean hasChildren(Object element) {
		return treeAdapter.hasChildren(element);
	}
	
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof IWorkspace) {
			return webHosts.toArray(NULL_OB);
		} else {
			return treeAdapter.getChildren(inputElement);
		}
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput == null)
			setNullWorkspace();
		else if(newInput instanceof IWorkspace)
			setNewWorkspaceAndViewer((IWorkspace) newInput, (StructuredViewer)viewer);
	}
	
	private void setNullWorkspace() {
		webHosts.clear();
		workspace = null;
	}
	
	private void setNewWorkspaceAndViewer(IWorkspace newWorkspace, StructuredViewer newViewer) {
		if(newWorkspace != workspace) {
			if(workspace != null) {
				workspace.getWebModel().removeChangeListener(modelListener);
			}
			workspace = newWorkspace;
			webHosts.clear();
			workspace.getWebModel().addChangeListenerAndPopulate(modelListener);
			this.viewer = newViewer;
		}
	}
	
	public void dispose() { 
	}
	
	private IEventHandler createModelListener() {
		return new IEventHandler() {
			public void handleEvent(IEvent event) {
				if(event instanceof NewWebEntityEvent)
					handleNewWebEntity((NewWebEntityEvent) event);
				else if(event instanceof UpdatedWebEntityEvent)
					handleUpdatedWebEntity((UpdatedWebEntityEvent) event);
			}
		};
	}
	
	private void handleNewWebEntity(NewWebEntityEvent event) {
		final IWebEntity entity = event.getEntity();
		if(entity instanceof IWebHost) {
			webHosts.add((IWebHost) entity);
		}
		refreshViewer();
	}
	
	private void handleUpdatedWebEntity(UpdatedWebEntityEvent event) {
		refreshElement(event.getEntity());
	}
	
	private void refreshElement(final IWebEntity element) {
		guiRun(new Runnable() {
			@Override
			public void run() {
				viewer.refresh(element, true);
			}
		});
	}
	
	private void refreshViewer() {
		guiRun(new Runnable() {

			@Override
			public void run() {
				viewer.refresh(true);				
			}
			
		});
	}
	
	private void guiRun(Runnable runnable) {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			synchronized(viewer) {
				viewer.getControl().getDisplay().asyncExec(runnable);
			}
		}
	}
}
