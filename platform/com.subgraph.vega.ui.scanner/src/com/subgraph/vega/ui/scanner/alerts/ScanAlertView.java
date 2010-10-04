package com.subgraph.vega.ui.scanner.alerts;

import java.util.logging.Logger;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.scanner.IScannerFactory;
import com.subgraph.vega.ui.scanner.Activator;

public class ScanAlertView extends ViewPart {
	public final static String ID = "com.subgraph.vega.views.alert";

	private final Logger logger = Logger.getLogger("scan-alert-view");
	private TreeViewer viewer;
	
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new ScanAlertContentProvider());
		viewer.setLabelProvider(new ScanAlertLabelProvider());
		final IScannerFactory scannerFactory = Activator.getDefault().getScannerFactory();
		if(scannerFactory == null) {
			logger.warning("Failed to obtain reference to Scanner Factory");
			return;
		}
		
		viewer.setInput(scannerFactory.getScanModel());
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

}
