package com.subgraph.vega.ui.scanner;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.subgraph.vega.ui.scanner.alerts.ScanAlertView;
import com.subgraph.vega.ui.scanner.info.ScanInfoView;

public class ScannerPerspectiveFactory implements IPerspectiveFactory {
	private final static String WEBSITE_VIEW = "com.subgraph.vega.views.website";


	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.addView(ScanInfoView.ID, IPageLayout.TOP, 0f, layout.getEditorArea());
		layout.addView(WEBSITE_VIEW, IPageLayout.LEFT, 0.25f, ScanInfoView.ID);
		layout.addView(ScanAlertView.ID, IPageLayout.BOTTOM, 0.80f, ScanInfoView.ID);
		layout.setEditorAreaVisible(false);
	}

}
