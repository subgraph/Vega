package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.model.alerts.NewScanInstanceEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTree;
import com.subgraph.vega.ui.util.ImageCache;

public class AlertTreeContentProvider implements ITreeContentProvider {

	private final IEventHandler alertListener = createAlertListener();

	private AlertTree tree;
	private Viewer viewer;
	private IWorkspace workspace;
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	@Override
	public void dispose() {		
		imageCache.dispose();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(newInput instanceof IWorkspace)
			setNewModelAndViewer((IWorkspace) newInput, viewer);
		else
			setNullModel();		
	}

	private void setNullModel() {
		tree = null;
		workspace = null;
	}
	
	private void setNewModelAndViewer(IWorkspace newWorkspace, Viewer newViewer) {
		if(newWorkspace != workspace) {
			if(workspace != null) {
				workspace.getScanAlertRepository().removeAlertListener(alertListener);
			}
			workspace = newWorkspace;

			tree = new AlertTree(workspace);
			this.viewer = newViewer;
			workspace.getScanAlertRepository().addAlertListenerAndPopulate(alertListener);
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(tree != null) {
			return tree.getChildren();
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IAlertTreeNode) {
			return ((IAlertTreeNode) parentElement).getChildren();
		} else {
			return new Object[0];
		}
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof IAlertTreeNode) {
			return ((IAlertTreeNode) element).hasChildren();
		}
		return false;
	}
	
	private IEventHandler createAlertListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof NewScanAlertEvent) {
					handleNewScanAlert((NewScanAlertEvent) event);
				} else if (event instanceof NewScanInstanceEvent) {
					handleNewScanInstance((NewScanInstanceEvent) event);
				}
			}
		};
	}
	
	private void handleNewScanAlert(NewScanAlertEvent event) {
		if(tree != null) {
			tree.addAlert(event.getAlert());
			refreshViewer();
		}
	}
	
	private void handleNewScanInstance(NewScanInstanceEvent event) {
		if(tree != null) {
			tree.addScan(event.getScanInstance());
			refreshViewer();
		}
	}

	private void refreshViewer() {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			synchronized(viewer) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						viewer.refresh();						
					}
				});
			}
		}
	}
}
