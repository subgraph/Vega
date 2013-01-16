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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.scope.ITargetScopeManager;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.ui.web.Activator;

public class WebsiteView extends ViewPart implements IDoubleClickListener {

	private static class UnvisitedFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if(element instanceof IWebEntity) {
				final IWebEntity entity = (IWebEntity) element;
				return entity.isVisited();
			}
			return false;
		}
	}
	private TreeViewer viewer;
	private DrillDownAdapter drillDown;
	private WebsiteLabelProvider labelProvider;
	private ITargetScopeManager scopeManager;
	private IEventHandler scopeChangeHandler;
	private AbstractScopeAction addAction;
	private AbstractScopeAction excludeAction;
	
	private ViewerFilter scopeFilter;
	private boolean filterUnvisited;
	private boolean filterByScope;

	@Override
	public void createPartControl(Composite parent) {
		scopeChangeHandler = createScopeChangeHandler();
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new WebsiteContentProvider());
		labelProvider = new WebsiteLabelProvider();
		viewer.setLabelProvider(labelProvider);
		final IModel model = Activator.getDefault().getModel();
		if(model != null) {
			final IWorkspace currentWorkspace = model.addWorkspaceListener(new IEventHandler() {

				@Override
				public void handleEvent(IEvent event) {
					if(event instanceof WorkspaceOpenEvent)
						handleWorkspaceOpen((WorkspaceOpenEvent) event);
					else if(event instanceof WorkspaceCloseEvent)
						handleWorkspaceClose((WorkspaceCloseEvent) event);
					else if(event instanceof WorkspaceResetEvent)
						handleWorkspaceReset((WorkspaceResetEvent) event);
				}
			});
			if(currentWorkspace != null) {
				setScopeManager(currentWorkspace.getTargetScopeManager());
				viewer.setInput(currentWorkspace);
			}
		}

		scopeFilter = new ScopeFilter(model);
		viewer.setSorter(new Sorter());
		viewer.addDoubleClickListener(this);
		final MenuManager menuManager = new MenuManager("");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(createMenuListener());
		addAction = new AddScopeAction(viewer, model);
		excludeAction = new ExcludeScopeAction(viewer, model);
		viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
		getSite().setSelectionProvider(viewer);
		drillDown = new DrillDownAdapter(viewer);
		contributeToActionBars();		
	}

	private void setScopeManager(ITargetScopeManager scopeManager) {
		if(this.scopeManager != null) {
			this.scopeManager.removeActiveScopeChangeListener(scopeChangeHandler);
		}
		this.scopeManager = scopeManager;
		labelProvider.setTargetScopeManager(scopeManager);
		if(scopeManager != null) {
			scopeManager.addActiveScopeChangeListener(scopeChangeHandler);
		}
	}

	private IEventHandler createScopeChangeHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				viewer.refresh();
			}
		};
	}
	public void setHideUnvisitedSites(boolean value) {
		filterUnvisited = value;
		setFiltersForFlags();
	}
	
	public void setFilterByScope(boolean value) {
		filterByScope = value;
		setFiltersForFlags();
	}
	
	private void setFiltersForFlags() {
		final List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
		if(filterUnvisited) {
			filters.add(new UnvisitedFilter());
		}
		if(filterByScope) {
			filters.add(scopeFilter);
		}
		viewer.setFilters(filters.toArray(new ViewerFilter[0]));
	}
	private IMenuListener createMenuListener() {
		return new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if(addAction.isEnabledForSelection()) {
					manager.add(addAction);
				}
				if(excludeAction.isEnabledForSelection()) {
					manager.add(excludeAction);
				}
			}
		};
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		setScopeManager(event.getWorkspace().getTargetScopeManager());
		viewer.setInput(event.getWorkspace());
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		setScopeManager(null);
		viewer.setInput(null);
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		setScopeManager(event.getWorkspace().getTargetScopeManager());
		viewer.setInput(null);
		viewer.setInput(event.getWorkspace());
	}
	
	@Override
	public void setFocus() {
		viewer.getTree().setFocus();		
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillToolBar(bars.getToolBarManager());
	}
	
	private void fillToolBar(IToolBarManager manager) {
		drillDown.addNavigationActions(manager);
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		final Object element = selection.getFirstElement();
		if(viewer.isExpandable(element)) {
			viewer.setExpandedState(element, !viewer.getExpandedState(element));
		}
	}
	
	public void collapseAll() {
		if(viewer != null) {
			viewer.collapseAll();
		}
	}

	public void expandAll() {
		if(viewer != null) {
			viewer.expandAll();
		}
	}
}
