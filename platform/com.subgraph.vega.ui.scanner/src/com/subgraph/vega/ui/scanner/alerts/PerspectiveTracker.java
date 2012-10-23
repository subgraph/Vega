package com.subgraph.vega.ui.scanner.alerts;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;

public class PerspectiveTracker implements IPerspectiveListener {
	private final StructuredViewer viewer;
	private final ProxyPerspectiveFilter proxyPerspectiveFilter;
	public PerspectiveTracker(IWorkbenchPage page, StructuredViewer viewer) {
		this.viewer = viewer;
		this.proxyPerspectiveFilter = new ProxyPerspectiveFilter();
		if(page.getPerspective() != null) {
			perspectiveActivated(page, page.getPerspective());
		}
	}
	
	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		if(perspective.getId().equals("com.subgraph.vega.perspectives.proxy")) {
			viewer.addFilter(proxyPerspectiveFilter);
		} else {
			viewer.removeFilter(proxyPerspectiveFilter);
		}
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {
	}

}
