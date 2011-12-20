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
package com.subgraph.vega.ui.scanner.alerts;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.model.alerts.NewScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTree;
import com.subgraph.vega.ui.util.ImageCache;

public class AlertTreeContentProvider implements ITreeContentProvider, IEventHandler {

	private final Timer blinkTimer = new Timer();
	private AlertTree tree;
	private TreeViewer viewer;
	private IWorkspace workspace;
	private IScanInstance activeScan;
	private IScanInstance proxyInstance;
	private int lastStatus;
	
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
				workspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
			}
			if(proxyInstance != null) {
				proxyInstance.removeScanEventListener(this);
			}

			workspace = newWorkspace;
			if(newWorkspace == null) {
				return;
			}
			
			tree = new AlertTree(workspace);
			this.viewer = (TreeViewer) newViewer;
			for(IScanInstance scan: workspace.getScanAlertRepository().getAllScanInstances()) {
				if (scan.getScanStatus() != IScanInstance.SCAN_CONFIG) {
					tree.addScan(scan);
					for(IScanAlert alert: scan.getAllAlerts()) {
						tree.addAlert(alert);
					}
				}
			}
			setActiveScan(workspace.getScanAlertRepository().addActiveScanInstanceListener(this));
			proxyInstance = workspace.getScanAlertRepository().getProxyScanInstance();
			proxyInstance.addScanEventListenerAndPopulate(this);
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

	private void handleNewScanAlert(NewScanAlertEvent event) {
		if(tree != null) {
			tree.addAlert(event.getAlert());
			refreshViewer();
		}
	}
	
	private void handleNewScanInstance(NewScanInstanceEvent event) {
		if(tree != null) {
//			tree.addScan(event.getScanInstance());
//			refreshViewer();
		}
	}

	private void handleActiveScanInstance(ActiveScanInstanceEvent event) {
		if(tree != null && event.getScanInstance() != null) {
			setActiveScan(event.getScanInstance());
		}
	}
	
	private void setActiveScan(IScanInstance scan) {
		if(activeScan != null) {
			activeScan.removeScanEventListener(this);
		}
	
		lastStatus = 0;
		
		if(scan != null) {
			activeScan = scan;
			activeScan.addScanEventListenerAndPopulate(this);

			AlertScanNode scanNode = tree.getScanNode(scan.getScanId());
			viewer.setSelection(new StructuredSelection(scanNode));
		}
	}

	private void handleScanStatusChange(ScanStatusChangeEvent event) {
		if(event.getStatus() != lastStatus) {
			lastStatus = event.getStatus();
			if (event.getStatus() == IScanInstance.SCAN_STARTING) {
				tree.addScan(event.getScanInstance());
			} else if (event.getStatus() == IScanInstance.SCAN_AUDITING) {
				AlertScanNode scanNode = tree.getScanNode(activeScan.getScanId());
				blinkTimer.scheduleAtFixedRate(createBlinkTask(scanNode), 0, 500);
			}
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

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof NewScanAlertEvent) {
			handleNewScanAlert((NewScanAlertEvent) event);
		} else if(event instanceof NewScanInstanceEvent) {
			handleNewScanInstance((NewScanInstanceEvent) event);
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof ScanStatusChangeEvent) {
			handleScanStatusChange((ScanStatusChangeEvent) event);
		}
	}
	
	private TimerTask createBlinkTask(final AlertScanNode scanNode) {
		return new TimerTask() {
			@Override
			public void run() {
				if(scanNode.getScanInstance().getScanStatus() == IScanInstance.SCAN_AUDITING) {
					updateScanNode(scanNode);
				} else {
					this.cancel();
				}				
			}
		};
	}
	
	private void updateScanNode(final AlertScanNode node) {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			synchronized(viewer) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						viewer.update(node, null);
					}
				});
			}
		}
	}
}
