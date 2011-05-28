package com.subgraph.vega.ui.scanner.alerts;

import java.util.Date;
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
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
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
				final int cat1 = category(e1);
				final int cat2 = category(e2);
				if(cat1 != cat2) {
					return cat1 - cat2;
				}
				
				if(!((e1 instanceof AlertScanNode) && (e2 instanceof AlertScanNode))) {
					return super.compare(viewer, e1, e2);
				} else {
					return compareAlertNodes((AlertScanNode)e1, (AlertScanNode)e2);
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
