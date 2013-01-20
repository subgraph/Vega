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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.model.alerts.RemoveScanAlertsEvent;
import com.subgraph.vega.api.model.alerts.RemoveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTree;
import com.subgraph.vega.ui.util.images.ImageCache;

public class AlertTreeContentProvider implements ITreeContentProvider, IEventHandler {
	private static final long BLINK_INTERVAL = 1000; // interval in milliseconds for active scan icon blink 
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private final Timer blinkTimer = new Timer();
	private final Map<Long, Integer> lastStatusMap = new TreeMap<Long, Integer>(); /** Map of active scan statuses, keyed by scan ID */
	private AlertTree tree;
	private TreeViewer viewer; // guarded by this
	private IWorkspace workspace;
	private IScanInstance proxyInstance;
	private List<AlertScanNode> activeList = new ArrayList<AlertScanNode>(); // guarded by this
	private boolean activeBlinkState; // guarded by this

	@Override
	public void dispose() {		
		imageCache.dispose();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		synchronized(this) {
			this.viewer = (TreeViewer) viewer;
			if(newInput instanceof IWorkspace)
				setNewModelAndViewer((IWorkspace) newInput);
			else
				setNullModel();		
		}
	}

	private void setNullModel() {
		tree = null;
		workspace = null;
		lastStatusMap.clear();
		activeList.clear();
	}
	
	private void setNewModelAndViewer(IWorkspace newWorkspace) {
		if(newWorkspace != workspace) {
			if(workspace != null) {
				workspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
			}
			if(proxyInstance != null) {
				proxyInstance.removeScanEventListener(this);
			}

			lastStatusMap.clear();
			activeList.clear();
			workspace = newWorkspace;
			if(newWorkspace == null) {
				return;
			}
			
			tree = new AlertTree(workspace);
			for(IScanInstance scan: workspace.getScanAlertRepository().getAllScanInstances()) {
				if (scan.getScanStatus() != IScanInstance.SCAN_CONFIG) {
					tree.addScan(scan);
					for(IScanAlert alert: scan.getAllAlerts()) {
						tree.addAlert(alert);
					}
					
				}
			}
			for (IScanInstance scanInstance: workspace.getScanAlertRepository().addActiveScanInstanceListener(this)) {
				addActiveScan(scanInstance);
			}
			proxyInstance = workspace.getScanAlertRepository().getProxyScanInstance();
			lastStatusMap.put(proxyInstance.getScanId(), -1);
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

	public void removeAlert(IScanAlert alert) {
		if(tree != null) {
			tree.removeAlert(alert);
			refreshViewer();
		}
	}
	private void handleNewScanAlert(NewScanAlertEvent event) {
		if(tree != null) {
			tree.addAlert(event.getAlert());
			refreshViewer();
		}
	}
	
	private void handleRemoveScanAlerts(RemoveScanAlertsEvent event) {
		if(tree != null) {
			tree.removeAlerts(event.getRemovedEvents());
			refreshViewer();
		}
	}
	
	private void handleActiveScanInstance(final ActiveScanInstanceEvent event) {
		synchronized(this) {
			if (viewer != null) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (tree != null && event.getScanInstance() != null) {
							addActiveScan(event.getScanInstance());
						}
					}
				});
			}
		}
	}
	
	private void handleRemoveScanInstance(RemoveScanInstanceEvent event) {
		if(tree != null && !event.getScanInstance().isActive()) {
			tree.removeScan(event.getScanInstance());
			refreshViewer();
			final AlertScanNode scanNode = chooseScanNode();
			if(scanNode != null) {
				viewer.setSelection(new StructuredSelection(scanNode));
			}
		}
	}
	
	private AlertScanNode chooseScanNode() {
		if(tree == null) {
			return null;
		}
		List<AlertScanNode> scanNodes = tree.getScanNodes();
		if(scanNodes.size() == 0) {
			return null;
		}
		
		for(AlertScanNode node: scanNodes) {
			if(node.getScanInstance().isActive()) {
				return node;
			}
		}
		return scanNodes.get(0);
	}
	
	private void addActiveScan(IScanInstance scan) {
		if(scan != null) {
			lastStatusMap.put(scan.getScanId(), -1);
			scan.addScanEventListenerAndPopulate(this);
			AlertScanNode scanNode = tree.addScan(scan);
			viewer.refresh();
			viewer.setSelection(new StructuredSelection(scanNode));
		}
	}

	private void handleScanStatusChange(ScanStatusChangeEvent event) {
		final int lastStatus = lastStatusMap.get(event.getScanInstance().getScanId());
		final int status = event.getStatus();
		if (lastStatus != status) {
			final long scanId = event.getScanInstance().getScanId();  
			lastStatusMap.put(scanId, status);
			if (scanId != IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) {
				if (event.getScanInstance().isActive() == true) {
					final AlertScanNode scanNode = tree.getScanNode(event.getScanInstance().getScanId());
					synchronized(this) {
						if (!activeList.contains(scanNode)) {
							activeList.add(scanNode);
							if (activeList.size() == 1) {
								blinkTimer.scheduleAtFixedRate(createBlinkTask(), 0, BLINK_INTERVAL);
							}
						}
					}
				} else if (event.getScanInstance().isComplete() == true) {
					synchronized(this) {
						activeList.remove(tree.getScanNode(scanId));
					}
				}
			}
			refreshViewer();
		}
	}

	private void refreshViewer() {
		synchronized(this) {
			if (viewer != null && !viewer.getControl().isDisposed()) {
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
		} else if(event instanceof RemoveScanAlertsEvent) {
			handleRemoveScanAlerts((RemoveScanAlertsEvent) event);
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof RemoveScanInstanceEvent) { 
			handleRemoveScanInstance((RemoveScanInstanceEvent) event);
		} else if(event instanceof ScanStatusChangeEvent) {
			handleScanStatusChange((ScanStatusChangeEvent) event);
		}
	}
	
	private TimerTask createBlinkTask() {
		return new TimerTask() {
			@Override
			public void run() {
				synchronized(AlertTreeContentProvider.this) {
					if (activeList.size() != 0) {
						activeBlinkState = !activeBlinkState;
					} else {
						activeBlinkState = false;
						cancel();
					}
					updateScanNodes();
				}
			}
		};
	}
	
	/**
	 * Must be invoked within a synchronized block.
	 */
	private void updateScanNodes() {
		if(viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					for (AlertScanNode node: activeList) {
						viewer.update(node, null);
					}
				}
			});
		}
	}

	public boolean isBlinkStateActive() {
		synchronized(this) {
			return activeBlinkState;
		}
	}
	
}
