package com.subgraph.vega.ui.scanner.alerts;

import java.util.logging.Logger;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;

public class ScanAlertView extends ViewPart implements IDoubleClickListener {
	public final static String ID = "com.subgraph.vega.views.alert";

	private final Logger logger = Logger.getLogger("scan-alert-view");
	private TreeViewer viewer;
	private IWorkspace currentWorkspace;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new AlertTreeContentProvider());
		viewer.setLabelProvider(new AlertTreeLabelProvider());
		viewer.addDoubleClickListener(this);
		viewer.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if(!(e1 instanceof AlertScanNode && e2 instanceof AlertScanNode)) {
					return super.compare(viewer, e1, e2);
				} else {
					return compareAlertNodes((AlertScanNode)e1, (AlertScanNode)e2);
				}
			}
		});
		getSite().setSelectionProvider(viewer);

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

	private int compareAlertNodes(AlertScanNode n1, AlertScanNode n2) {
		if(n1.getScanInstance() == null || n2.getScanInstance() == null) {
			return (int) (n1.getScanId() - n2.getScanId());
		} else {
			long t1 = n1.getScanInstance().getStartTime().getTime();
			long t2 = n2.getScanInstance().getStartTime().getTime();
			return (t1 < t2) ? (1) : (-1);
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
}
