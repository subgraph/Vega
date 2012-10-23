package com.subgraph.vega.ui.scanner.alerts;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.ScanPauseStateChangedEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertTitleNode;
import com.subgraph.vega.ui.scanner.commands.PauseStateSourceProvider;
import com.subgraph.vega.ui.scanner.commands.ScannerStateSourceProvider;
import com.subgraph.vega.ui.scanner.info.ScanInfoView;

public class SelectionTracker implements ISelectionChangedListener {
	private final Logger logger = Logger.getLogger("scan-alert-view");
	private final IWorkbenchPage page;
	private final IEventHandler scanEventHandler;
	
	private IScanInstance selectedScanInstance;
	
	public SelectionTracker(IWorkbenchPage page) {
		this.page = page;
		this.scanEventHandler = createScanEventHandler();
	}
	
	private IEventHandler createScanEventHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if( (event instanceof ScanPauseStateChangedEvent) 
						|| (event instanceof ScanStatusChangeEvent)) {
					updateSourceProviders(selectedScanInstance);
				}
			}
		};
	}
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		handleSelection((IStructuredSelection) event.getSelection());
	}
	
	private void handleSelection(IStructuredSelection selection) {
		final Object item = selection.getFirstElement();
		if(item instanceof IAlertTreeNode) {
			setSelectedScanInstance( ((IAlertTreeNode) item).getScanInstance() );
			if(item instanceof AlertTitleNode) {
				activateInfoView();
			}
		} else if(item instanceof IScanAlert) {
			setSelectedScanInstance( ((IScanAlert)item).getScanInstance() );
			activateInfoView();
		} else {
			setSelectedScanInstance(null);
		}
	}

	private void activateInfoView() {
		try {
			page.showView(ScanInfoView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} catch (PartInitException e) {
			logger.log(Level.WARNING, "Failed to open Scan Info view", e);
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
}
