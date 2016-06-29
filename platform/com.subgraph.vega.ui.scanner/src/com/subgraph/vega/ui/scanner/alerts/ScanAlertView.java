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

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.ExportWizard;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.export.AlertExporter;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertHostNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertSeverityNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTitleNode;
import com.subgraph.vega.ui.util.export.AlertExportWizard;
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
	private boolean ignoreEvents;
	
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
		createContextMenu(viewer);
		
		final IModel model = Activator.getDefault().getModel();
		if(model == null) {
			logger.warning("Failed to obtain reference to model");
			return;
		}
		scopeTracker = new ScopeTracker(viewer);
		WorkspaceTracker.create(model, this, scopeTracker);
		getSite().getWorkbenchWindow().addPerspectiveListener(new PerspectiveTracker(getSite().getPage(), viewer));
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
			ignoreEvents = true;
			workspace.getScanAlertRepository().getProxyScanInstance().addScanEventListenerAndPopulate(this);
			ignoreEvents = false;
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
			setLabelImage(imageCache.get(ALERT_VIEW_ICON));
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
		final IScanInstance scanInstance = getScanInstanceForSelection((IStructuredSelection) viewer.getSelection());
		if(scanInstance == null) {
			return null;
		} else {
			return scanInstance.getScan();
		}
	}

	private IScanInstance getScanInstanceForSelection(IStructuredSelection selection) {
		if(selection == null || selection.isEmpty()) {
			return null;
		}
		
		final Object element = selection.getFirstElement();
		
		if(element instanceof IScanAlert) {
			return ((IScanAlert)element).getScanInstance();
		} else if(element instanceof IAlertTreeNode) {
			return ((IAlertTreeNode)element).getScanInstance();
		} else {
			return null;
		}
	}

	public void setTitleImage(Image image) {
		if(image != null && !image.isDisposed()) {
			super.setTitleImage(image);
		}
	}
	
	public void setFilterByScope(boolean enabled) {
		scopeTracker.setFilterByScopeEnabled(enabled);
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof NewScanAlertEvent) {
			if(!isViewVisible && !ignoreEvents) {
				startNotifier();
			}
		}
	}
	
	private void createContextMenu(TreeViewer viewer) {
		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		final Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		if(selection == null) {
			return;
		}
		final Object ob = selection.getFirstElement();
		if(ob instanceof IScanAlert) {
			createContextMenuForScanAlert(manager, (IScanAlert) ob);
		} else if (ob instanceof AlertScanNode) {
			createContextMenuForScanNode(manager, (AlertScanNode) ob);
		} else if (ob instanceof IAlertTreeNode) {
			createContextMenuForAlertTreeNode(manager, (IAlertTreeNode) ob);
		}
	}
	
	private void createContextMenuForScanAlert(IMenuManager manager, final IScanAlert alert) {
		final Action action = new Action() {
			@Override
			public void run() {
				alert.getScanInstance().removeAlert(alert);
			}
		};
		action.setText("Remove alert");
		manager.add(action);
		
	}
	
	private void createContextMenuForScanNode(IMenuManager manager, final AlertScanNode node) {
		if(node.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID || node.getScanInstance().isActive()) {
			return;
		}

		final Action action = new Action() {
			@Override
			public void run() {
				currentWorkspace.getScanAlertRepository().removeScanInstance(node.getScanInstance());
			}
		};
		action.setText("Remove Scan");
		manager.add(action);
		
		final Action exportAction = new Action() {
			@Override
			public void run() {
			//	AlertExporter exporter = new AlertExporter(currentWorkspace);
				//exporter.exportbyScanInstance(node.getScanInstance());			
				WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				      new AlertExportWizard(node.getScanInstance()));				
				wizardDialog.open();
				
			  }
				
				  
			
		};
		exportAction.setText("Export Scan Results");
		manager.add(exportAction);
	}
	
	private void createContextMenuForAlertTreeNode(IMenuManager manager, final IAlertTreeNode node) {
		final Collection<IScanAlert> alerts = node.getAlerts();
		final Action action = new Action() {
			@Override 
			public void run() {
				node.getScanInstance().removeAlerts(alerts);
			}
		};
		action.setText(getAlertTreeNodeString(node) + getAlertCountString(alerts.size()));
		manager.add(action);
		
	}

	private String getAlertCountString(int n) {
		if(n == 1) {
			return "1 alert";
		} else {
			return Integer.toString(n) + " alerts";
		}
	}
	
	private String getAlertTreeNodeString(IAlertTreeNode node) {
		if(node instanceof AlertTitleNode) {
			return "Remove ";
		} else if (node instanceof AlertSeverityNode) {
			return "Remove severity node with ";
		} else if (node instanceof AlertHostNode) {
			return "Remove host node with ";
		} else {
			return "Remove ";
		}
	}
}
