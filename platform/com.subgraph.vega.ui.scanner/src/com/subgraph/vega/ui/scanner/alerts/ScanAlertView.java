package com.subgraph.vega.ui.scanner.alerts;

import java.util.logging.Logger;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.ui.scanner.Activator;

public class ScanAlertView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.alert";

	private final Logger logger = Logger.getLogger("scan-alert-view");
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AlertTreeContentProvider());
		viewer.setLabelProvider(new AlertTreeLabelProvider());
		final IModel model = Activator.getDefault().getModel();
		if(model == null) {
			logger.warning("Failed to obtain reference to model");
			return;
		}
		final IWorkspace currentWorkspace = model.addWorkspaceListener(new IEventHandler() {
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
		if(currentWorkspace != null)
			viewer.setInput(currentWorkspace);
		getSite().setSelectionProvider(viewer);
	}

	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		viewer.setInput(event.getWorkspace());
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		viewer.setInput(null);
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		viewer.setInput(null);
		viewer.setInput(event.getWorkspace());
	}
	
	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}
}
