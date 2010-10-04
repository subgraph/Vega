package com.subgraph.vega.ui.scanner.info;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.scanner.model.IScanAlert;

public class ScanInfoView extends ViewPart {
	public static String ID = "com.subgraph.vega.views.scaninfo";
	
	private Browser browser;
	private final AlertRenderer renderer = new AlertRenderer();
	
	public ScanInfoView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		
		getSite().getPage().addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o instanceof IScanAlert) {		
					IScanAlert alert = (IScanAlert) o;
					displayAlert(alert);
				}				
			}
			
		});
	}

	private void displayAlert(IScanAlert alert) {
		String html = renderer.render(alert);
		if(html != null && !browser.isDisposed())
			browser.setText(html, true);
	}
	
	@Override
	public void setFocus() {
		browser.setFocus();
	}

}
