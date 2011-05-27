package com.subgraph.vega.ui.web.views;

import org.eclipse.jface.action.IToolBarManager;
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

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new WebsiteContentProvider());
		viewer.setLabelProvider(new WebsiteLabelProvider());
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
			if(currentWorkspace != null)
				viewer.setInput(currentWorkspace);
		}

		viewer.setSorter(new Sorter());
		viewer.addDoubleClickListener(this);
		getSite().setSelectionProvider(viewer);
		drillDown = new DrillDownAdapter(viewer);
		contributeToActionBars();		
	}

	public void setHideUnvisitedSites(boolean value) {
		if(value) {
			viewer.setFilters(new ViewerFilter[] { new UnvisitedFilter() });
		} else {
			viewer.setFilters(new ViewerFilter[0]);
		}
	}
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		viewer.setInput(event.getWorkspace());
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		viewer.setInput(null);
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
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
}
