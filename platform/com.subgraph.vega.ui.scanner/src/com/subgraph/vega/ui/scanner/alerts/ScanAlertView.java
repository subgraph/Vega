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

import java.util.Date;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.ScanPauseStateChangedEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertSeverityNode;
import com.subgraph.vega.ui.scanner.commands.PauseStateSourceProvider;
import com.subgraph.vega.ui.scanner.commands.ScannerStateSourceProvider;

public class ScanAlertView extends ViewPart implements IDoubleClickListener {
	public final static String ID = "com.subgraph.vega.views.alert";
	private final Logger logger = Logger.getLogger("scan-alert-view");
	
	private IEventHandler scanEventHandler;
	private TreeViewer viewer;
	private IWorkspace currentWorkspace;
	private IScanInstance selectedScanInstance;
	
	
	@Override
	public void createPartControl(Composite parent) {
		scanEventHandler = createScanEventHandler();
		viewer = new TreeViewer(parent);
		final AlertTreeContentProvider contentProvider = new AlertTreeContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new AlertTreeLabelProvider(contentProvider));
		viewer.addDoubleClickListener(this);
		viewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				final int cat1 = category(e1);
				final int cat2 = category(e2);
				if(cat1 != cat2) {
					return cat1 - cat2;
				}

				if((e1 instanceof AlertScanNode) && (e2 instanceof AlertScanNode)) {
					return compareAlertNodes((AlertScanNode)e1, (AlertScanNode)e2);
				} else if((e1 instanceof AlertSeverityNode) && (e2 instanceof AlertSeverityNode)) {
					final AlertSeverityNode asn1 = (AlertSeverityNode) e1;
					final AlertSeverityNode asn2 = (AlertSeverityNode) e2;
					return asn2.getSeverityIndex() - asn1.getSeverityIndex();
					
				} else {
					return super.compare(viewer, e1, e2);
				}
			}
			@Override
			public int category(Object element) {
				if(element instanceof AlertScanNode) {
					return (((AlertScanNode) element).getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) ? (0) : (1); 
				}
				return 0;
			}
			
		});
		getSite().setSelectionProvider(viewer);
		viewer.addSelectionChangedListener(createSelectionChangedListener());
		
		final IModel model = Activator.getDefault().getModel();
		if(model == null) {
			logger.warning("Failed to obtain reference to model");
			return;
		}
		currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
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
			viewer.setInput(currentWorkspace);
			selectFirstScan();
		}
	}

	private IEventHandler createScanEventHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ScanPauseStateChangedEvent) {
					updateSourceProviders(selectedScanInstance);
				} else if(event instanceof ScanStatusChangeEvent) {
					updateSourceProviders(selectedScanInstance);
				}
			}
			
		};
	}

	public void expandAll() {
		if (viewer != null) {
			viewer.expandAll();
		}
	}

	public void collapseAll() {
		if (viewer != null) {
			viewer.collapseAll();
		}
	}

	private int compareAlertNodes(AlertScanNode n1, AlertScanNode n2) {
		if((n1.getScanInstance() == null) || (n2.getScanInstance() == null)) {
			return (int) (n1.getScanId() - n2.getScanId());
		} else {
			final Date d1 = n1.getScanInstance().getStartTime();
			final Date d2 = n2.getScanInstance().getStartTime();
			if(d1 == null || d2 == null) {
				return 0;
			}
			return (d1.getTime() < d2.getTime()) ? (1) : (-1);
		}
	}
	
	private void selectFirstScan() {
		if(viewer.getTree().getItemCount() > 0) {
			final TreeItem item = viewer.getTree().getItem(0);
			viewer.setSelection(new StructuredSelection(item.getData()));
		} 
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		viewer.setInput(event.getWorkspace());
		selectFirstScan();
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		viewer.setInput(null);
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		viewer.setInput(null);
		viewer.setInput(event.getWorkspace());
		selectFirstScan();
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelection((IStructuredSelection) viewer.getSelection());
			}
		};
	}
	
	private void handleSelection(IStructuredSelection selection) {
		final Object item = selection.getFirstElement();
		if(item instanceof IAlertTreeNode) {
			setSelectedScanInstance( ((IAlertTreeNode) item).getScanInstance() );
		} else if(item instanceof IScanAlert) {
			setSelectedScanInstance( ((IScanAlert)item).getScanInstance() );
		} else {
			setSelectedScanInstance(null);
		}
	}
	
	private void setSelectedScanInstance(IScanInstance scanInstance) {
		if(selectedScanInstance != null) {
			selectedScanInstance.removeScanEventListener(scanEventHandler);
		} 
		selectedScanInstance = scanInstance;
		if(selectedScanInstance != null) {
			selectedScanInstance.addScanEventListenerAndPopulate(scanEventHandler);
		} 
		updateSourceProviders(scanInstance);
	}

	private void updateSourceProviders(IScanInstance scanInstance) {
		final ISourceProviderService sps = (ISourceProviderService) PlatformUI.getWorkbench().getService(ISourceProviderService.class);
		updatePauseStateSourceProvider((PauseStateSourceProvider) sps.getSourceProvider(PauseStateSourceProvider.PAUSE_STATE), scanInstance);
		updateScannerStateSourceProvider((ScannerStateSourceProvider) sps.getSourceProvider(ScannerStateSourceProvider.SCAN_SELECTION_STATE), scanInstance);
	}
	
	private void updatePauseStateSourceProvider(PauseStateSourceProvider provider, IScanInstance scanInstance) {
		if(provider != null) {
			provider.setSelectedScan(scanInstance);
		}
	}
	
	private void updateScannerStateSourceProvider(ScannerStateSourceProvider provider, IScanInstance scanInstance) {
		if(provider != null) {
			if(scanInstance != null) {
				provider.setScanSelectionIsActive(scanInstance.isActive());
			} else {
				provider.setScanSelectionIsActive(false);
			}
		}
	}
	
	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		final Object element = selection.getFirstElement();
		if(viewer.isExpandable(element)) {
			viewer.setExpandedState(element, !viewer.getExpandedState(element));
		}
	}

	public IScan getSelection() {
		final IAlertTreeNode node = (IAlertTreeNode)((IStructuredSelection) viewer.getSelection()).getFirstElement();
		if (node != null) {
			final IScanInstance scanInstance = node.getScanInstance();
			if (scanInstance != null) {
				return scanInstance.getScan();
			}
		}
		return null;
	}
}
