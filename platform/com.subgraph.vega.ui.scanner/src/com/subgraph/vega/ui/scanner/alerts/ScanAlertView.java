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
import java.util.logging.Logger;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

public class ScanAlertView extends ViewPart implements IDoubleClickListener, IEventHandler {
	public final static String ID = "com.subgraph.vega.views.alert";
	private final static String ALERT_VIEW_ICON = "icons/alert_view.png";
	private final Logger logger = Logger.getLogger("scan-alert-view");
	
	private final Timer blinkTimer = new Timer();
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	
	
	private IWorkspace currentWorkspace;
	private ScopeTracker scopeTracker;
	private IPartListener2 partListener;
	private TreeViewer viewer;
	private TimerTask blinkTask;
	private boolean isViewVisible;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		final AlertTreeContentProvider contentProvider = new AlertTreeContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new AlertTreeLabelProvider(contentProvider));
		viewer.addDoubleClickListener(this);
		viewer.setSorter(new ScanAlertSorter());
				
		getSite().setSelectionProvider(viewer);
		viewer.addSelectionChangedListener(new SelectionTracker(getSite().getPage()));
		
		final IModel model = Activator.getDefault().getModel();
		if(model == null) {
			logger.warning("Failed to obtain reference to model");
			return;
		}
		scopeTracker = new ScopeTracker(viewer);
		WorkspaceTracker.create(model, this, scopeTracker);
		getSite().getWorkbenchWindow().addPerspectiveListener(new PerspectiveTracker(viewer));
		partListener = createPartListener();
		getSite().getPage().addPartListener(partListener);
	}
	
		private IPartListener2 createPartListener() {
		return new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				if(ID.equals(partRef.getId())) {
					isViewVisible = true;
					stopNotifier();
				}
			}
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				if(ID.equals(partRef.getId())) {
					isViewVisible = false;
				}
			}

			@Override public void partOpened(IWorkbenchPartReference partRef) {}
			@Override public void partInputChanged(IWorkbenchPartReference partRef) {}
			@Override public void partDeactivated(IWorkbenchPartReference partRef) {}
			@Override public void partClosed(IWorkbenchPartReference partRef) {}
			@Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}
			@Override public void partActivated(IWorkbenchPartReference partRef) {}
		};
	}
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		imageCache.dispose();
		super.dispose();
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
	
	private void selectFirstScan() {
		if(viewer.getTree().getItemCount() > 0) {
			final TreeItem item = viewer.getTree().getItem(0);
			viewer.setSelection(new StructuredSelection(item.getData()));
		} 
	}

	public void workspaceChanged(IWorkspace workspace) {
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().getProxyScanInstance().removeScanEventListener(this);
		}
		currentWorkspace = workspace;
		viewer.setInput(workspace);
		if(workspace != null) {
			selectFirstScan();
			workspace.getScanAlertRepository().getProxyScanInstance().addScanEventListenerAndPopulate(this);
		}	
	}
	
	public void startNotifier() {
		if(blinkTask == null) {
			blinkTask = createBlinkTask();
			blinkTimer.scheduleAtFixedRate(blinkTask, 0, 500);
		}
	}

	public void stopNotifier() {
		if(blinkTask != null) {
			blinkTask.cancel();
			blinkTask = null;
		}
	}
	
	private TimerTask createBlinkTask() {
		return new TimerTask() {
			private boolean state;
			@Override
			public void run() {
				state = !state;
				if(state) {
					setLabelImage(imageCache.getDisabled(ALERT_VIEW_ICON));
				} else {
					setLabelImage(imageCache.get(ALERT_VIEW_ICON));
				}
			}
		};
	}
	
	private void setLabelImage(final Image image) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				setTitleImage(image);
			}
		});
	}
	
	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
		stopNotifier();
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
	
	public void setTitleImage(Image image) {
		super.setTitleImage(image);
	}
	
	public void setFilterByScope(boolean enabled) {
		scopeTracker.setFilterByScopeEnabled(enabled);
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof NewScanAlertEvent) {
			if(!isViewVisible) {
				startNotifier();
			}
		}
	}
	
}
