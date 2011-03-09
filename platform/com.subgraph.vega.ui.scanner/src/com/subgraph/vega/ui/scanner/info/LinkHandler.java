package com.subgraph.vega.ui.scanner.info;

import java.util.logging.Logger;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.ui.http.requestviewer.HttpRequestView;

public class LinkHandler extends BrowserFunction {
	final private static Logger logger = Logger.getLogger("alertView");
	public LinkHandler(Browser browser) {
		super(browser, "linkClick");
	}
	
	@Override
	public Object function (Object[] arguments) {
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.subgraph.vega.views.http");
			if(view instanceof HttpRequestView && arguments[0] instanceof String) {
				final HttpRequestView requestView = (HttpRequestView) view;
				try {
					long id = Long.parseLong((String) arguments[0]);
					requestView.focusOnRecord(id);
				} catch (NumberFormatException e) {
					
				}
			}
		} catch (PartInitException e) {
			logger.warning("Failed to open HTTP request viewer");
		}
		return null;
	}

}
