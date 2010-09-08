package com.subgraph.vega.ui.web.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.ui.web.Activator;

public class WebsiteView extends ViewPart {
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDown;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new WebsiteContentProvider());
		viewer.setLabelProvider(new WebsiteLabelProvider());
		viewer.setInput(Activator.getDefault().getModel());
		viewer.setSorter(new Sorter());
		getSite().setSelectionProvider(viewer);
		drillDown = new DrillDownAdapter(viewer);
		contributeToActionBars();		
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();		
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillToolBar(bars.getToolBarManager());
	}
	
	private void fillToolBar(IToolBarManager manager) {
		drillDown.addNavigationActions(manager);
	}
	

}
