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
package com.subgraph.vega.ui.scanner.info;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.RemoveScanAlertsEvent;
import com.subgraph.vega.api.model.alerts.RemoveScanInstanceEvent;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.ScanAlertView;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTitleNode;
import com.subgraph.vega.ui.scanner.dashboard.DashboardPane;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

public class ScanInfoView extends ViewPart implements IEventHandler {
	private final Logger logger = Logger.getLogger("info-view");

	public static String ID = "com.subgraph.vega.views.scaninfo";
	
	private Browser browser;
	private IScanAlert currentBrowserAlert;
	private DashboardPane dashboard;
	private Composite contentPanel;
	private final AlertRenderer renderer;
	private StackLayout stackLayout = new StackLayout();
	private BrowserFunction linkClick;
	private IWorkspace currentWorkspace;
	
	public ScanInfoView() {
		final TemplateLoader loader = createTemplateLoader();
		if(loader == null)
			renderer = null;
		else
			renderer = new AlertRenderer(loader);
	}
	
	private TemplateLoader createTemplateLoader() {
		final IPathFinder pathFinder = Activator.getDefault().getPathFinder();
		if(pathFinder == null)
			throw new IllegalStateException("Cannot find templates to render because path finder service is not available");
		final File templateDirectory = new File(pathFinder.getDataDirectory(), "templates");
		try {
			return new FileTemplateLoader(templateDirectory);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to open template directory: "+ e.getMessage());
			return null;
		}
		
	}

	@Override
	public void createPartControl(Composite parent) {
		contentPanel = new Composite(parent, SWT.NONE);
		contentPanel.setLayout(stackLayout);
		dashboard = new DashboardPane(contentPanel);
		browser = new Browser(contentPanel, SWT.NONE);
		// Some day I will regret this, but it's currently the only way to have clickable links in the alert viewer
		browser.setJavascriptEnabled(true);
		linkClick = new LinkHandler(browser, getSite());
		
		getSite().getPage().addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if(!(selection instanceof IStructuredSelection))
					return;
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o instanceof IScanAlert) {		
					IScanAlert alert = (IScanAlert) o;
					displayAlert(alert);
				} else if (o instanceof AlertScanNode) {
					final AlertScanNode node = (AlertScanNode) o;
					displayScanSummary(node.getScanInstance());
				} else if (o instanceof AlertTitleNode) {
					final AlertTitleNode node = (AlertTitleNode) o;
					if(node.getAlertCount() == 1) {
						displayAlert(node.getFirstAlert());
					}
				}
			}
		});
		
		stackLayout.topControl = dashboard;
		contentPanel.layout();
		
		final IModel model = Activator.getDefault().getModel();
		if(model != null) {
			setCurrentWorkspace(model.addWorkspaceListener(this));
		}
		IStructuredSelection selection = (IStructuredSelection) getSite().getPage().getSelection(ScanAlertView.ID);
		if(selection != null && selection.getFirstElement() instanceof AlertScanNode) {
			AlertScanNode node = (AlertScanNode) selection.getFirstElement();
			displayScanSummary(node.getScanInstance());
		}
	}
	
	private void resetState() {
		browser.setText("");
		dashboard.displayScanInstance(null);
		showDashboard();
	}

	private void displayAlert(IScanAlert alert) {
		String html = renderer.render(alert);
		if(html != null && !browser.isDisposed()) {
			browser.setText(html, true);
			currentBrowserAlert = alert;
		}
		stackLayout.topControl = browser;
		contentPanel.layout();
	}
	
	private void displayScanSummary(final IScanInstance scan) {
		if(scan == null) {
			return;
		}
		if(!dashboard.getDisplay().isDisposed()) {
			dashboard.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					dashboard.displayScanInstance(scan);
					showDashboard();
				}
				
			});
		}
	}
	public void showDashboard() {
		currentBrowserAlert = null;
		stackLayout.topControl = dashboard;
		if(!contentPanel.isDisposed()) {
			contentPanel.layout();
		}
	}
	
	@Override
	public void setFocus() {
		browser.setFocus();
	}
	
	public void dispose() {
		if(linkClick != null)
			linkClick.dispose();
		super.dispose();
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof WorkspaceOpenEvent) {
			handleWorkspaceOpen((WorkspaceOpenEvent) event);
		} else if(event instanceof WorkspaceCloseEvent) {
			handleWorkspaceCloseEvent((WorkspaceCloseEvent) event);
		} else if(event instanceof WorkspaceResetEvent) {
			handleWorkspaceResetEvent((WorkspaceResetEvent) event);
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof RemoveScanAlertsEvent) {
			handleRemoveScanAlertsEvent((RemoveScanAlertsEvent) event);
		} else if(event instanceof RemoveScanInstanceEvent) {
			handleRemoveScanInstanceEvent((RemoveScanInstanceEvent) event);
			
		}
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		setCurrentWorkspace(event.getWorkspace());
	}
	
	private void handleWorkspaceCloseEvent(WorkspaceCloseEvent event) {
		setCurrentWorkspace(null);
		resetState();
	}
	
	private void handleWorkspaceResetEvent(WorkspaceResetEvent event) {
		setCurrentWorkspace(event.getWorkspace());
		resetState();
	}
	
	private void handleActiveScanInstance(ActiveScanInstanceEvent event) {
		setActiveScanInstance(event.getScanInstance());
	}
	
	private void handleRemoveScanAlertsEvent(RemoveScanAlertsEvent event) {
		if(currentBrowserAlert != null) {
			for(IScanAlert alert: event.getRemovedEvents()) {
				if(alert == currentBrowserAlert) {
					resetState();
				}
			}
		}
		dashboard.reset();
	}
	
	private void handleRemoveScanInstanceEvent(RemoveScanInstanceEvent event) {
		if(currentBrowserAlert != null && currentBrowserAlert.getScanId() == event.getScanInstance().getScanId()) {
			resetState();
		}
		if(stackLayout.topControl == dashboard && dashboard.getScanInstance() == event.getScanInstance()) {
			resetState();
		}
	}

	private void setCurrentWorkspace(IWorkspace workspace) {
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
		}
		if(workspace == null) {
			setActiveScanInstance(null);
			currentWorkspace = null;
			return;
		}
		
		final List<IScanInstance> activeScanInstanceList = workspace.getScanAlertRepository().addActiveScanInstanceListener(this);
		if (activeScanInstanceList.size() != 0) {
			setActiveScanInstance(activeScanInstanceList.get(0));
		}
		currentWorkspace = workspace;
	}
	
	private void setActiveScanInstance(IScanInstance scanInstance) {
		displayScanSummary(scanInstance);
	}
}
