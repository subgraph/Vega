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

import java.util.Map;
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
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTree;
import com.subgraph.vega.ui.util.images.ImageCache;

public class AlertTreeContentProvider implements ITreeContentProvider, IEventHandler {
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
//	private final Timer blinkTimer = new Timer();
	private final Map<Long, Integer> lastStatusMap = new TreeMap<Long, Integer>(); /** Map of active scan statuses, keyed by scan ID */
	private AlertTree tree;
	private TreeViewer viewer;
	private IWorkspace workspace;
	private IScanInstance proxyInstance;
	
	@Override
	public void dispose() {		
		imageCache.dispose();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		synchronized(this) {
			this.viewer = (TreeViewer) viewer;
		}
		if(newInput instanceof IWorkspace)
			setNewModelAndViewer((IWorkspace) newInput);
		else
			setNullModel();		
	}

	private void setNullModel() {
		tree = null;
		workspace = null;
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

	private void handleNewScanAlert(NewScanAlertEvent event) {
		if(tree != null) {
			tree.addAlert(event.getAlert());
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
	
	private void addActiveScan(IScanInstance scan) {
		if(scan != null) {
			lastStatusMap.put(scan.getScanId(), -1);
			scan.addScanEventListenerAndPopulate(this);
			AlertScanNode scanNode = tree.addScan(scan);
			viewer.setSelection(new StructuredSelection(scanNode));
		}
	}

	private void handleScanStatusChange(ScanStatusChangeEvent event) {
		final int lastStatus = lastStatusMap.get(event.getScanInstance().getScanId());
		final int status = event.getStatus();
		if (lastStatus != status) {
			lastStatusMap.put(event.getScanInstance().getScanId(), status);
//			if (event.getStatus() == IScanInstance.SCAN_AUDITING) {
//				AlertScanNode scanNode = tree.getScanNode(event.getScanInstance().getScanId());
//				blinkTimer.scheduleAtFixedRate(createBlinkTask(scanNode), 0, 500);
//			} 
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
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof ScanStatusChangeEvent) {
			handleScanStatusChange((ScanStatusChangeEvent) event);
		}
	}
	
//	private TimerTask createBlinkTask(final AlertScanNode scanNode) {
//		return new TimerTask() {
//			@Override
//			public void run() {
//				if(scanNode.getScanInstance().getScanStatus() == IScanInstance.SCAN_AUDITING) {
//					updateScanNode(scanNode);
//				} else {
//					this.cancel();
//				}				
//			}
//		};
//	}
//	
//	private void updateScanNode(final AlertScanNode node) {
//		if(viewer != null && !viewer.getControl().isDisposed()) {
//			synchronized(viewer) {
//				viewer.getControl().getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						viewer.update(node, null);
//					}
//				});
//			}
//		}
//	}

}
