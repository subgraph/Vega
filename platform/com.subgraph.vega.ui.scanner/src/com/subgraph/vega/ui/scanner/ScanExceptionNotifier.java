package com.subgraph.vega.ui.scanner;

import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.ScanExceptionEvent;

public class ScanExceptionNotifier implements IEventHandler {
	
	private final Set<String> hostSet = new HashSet<String>();
	private IWorkspace currentWorkspace;
	private IScanInstance activeScanInstance;
	
	ScanExceptionNotifier() {
		currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(this);
		if(currentWorkspace != null) {
			activeScanInstance = currentWorkspace.getScanAlertRepository().addActiveScanInstanceListener(this);
			if(activeScanInstance != null) {
				activeScanInstance.addScanEventListenerAndPopulate(this);
			}
		}
	}

	public void stop() {
		setActiveScanInstance(null);
		setCurrentWorkspace(null);
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof WorkspaceOpenEvent) {
			handleWorkspaceOpen((WorkspaceOpenEvent) event);
		} else if(event instanceof WorkspaceResetEvent) {
			handleWorkspaceReset((WorkspaceResetEvent) event);
		} else if(event instanceof WorkspaceCloseEvent) {
			handleWorkspaceClose((WorkspaceCloseEvent) event);
		} else if(event instanceof ActiveScanInstanceEvent) {
			handleActiveScanInstance((ActiveScanInstanceEvent) event);
		} else if(event instanceof ScanExceptionEvent) {
			handleScanException((ScanExceptionEvent) event);
		}
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		setCurrentWorkspace(event.getWorkspace());
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		setCurrentWorkspace(event.getWorkspace());
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		setCurrentWorkspace(null);
	}
	
	private void handleActiveScanInstance(ActiveScanInstanceEvent event) {
		setActiveScanInstance(event.getScanInstance());
	}
	
	private void handleScanException(ScanExceptionEvent event) {
		final URI uri = event.getRequest().getURI();
		final String host = uri.getHost();
		synchronized (hostSet) {
			if(hostSet.contains(host)) {
				return;
			}
			hostSet.add(host);
		}
		
		final StringBuilder sb = new StringBuilder();
		sb.append("Error requesting ");
		sb.append(uri.toString());
		sb.append("\n\n");
		sb.append(exceptionToMessage(event.getException()));
		openDialog(sb.toString());
	}
	
	private String exceptionToMessage(Throwable exception) {
		if(exception instanceof UnknownHostException) {
			return "Host '" + exception.getMessage() +"' not found.";
		} else {
			return exception.getMessage();
		}
	}
	
	private void openDialog(final String message) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				final Shell shell = display.getActiveShell();
				if(!(shell == null || shell.isDisposed())) {
					MessageDialog.openError(shell, "Scan Error", message);
				}
			}
		});
	}
	
	
	private void setCurrentWorkspace(IWorkspace workspace) {
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().removeActiveScanInstanceListener(this);
		}
		currentWorkspace = workspace;
		if(currentWorkspace != null) {
			currentWorkspace.getScanAlertRepository().addActiveScanInstanceListener(this);
		}
		synchronized (hostSet) {
			hostSet.clear();
		}
	}

	private void setActiveScanInstance(IScanInstance scan) {
		if(activeScanInstance != null) {
			activeScanInstance.removeScanEventListener(this);
		}
		activeScanInstance = scan;
		if(scan != null) {
			activeScanInstance.addScanEventListenerAndPopulate(this);
		}
		synchronized (hostSet) {
			hostSet.clear();
		}
	}
}
