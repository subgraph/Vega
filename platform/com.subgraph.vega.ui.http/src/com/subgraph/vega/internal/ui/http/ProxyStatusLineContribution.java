package com.subgraph.vega.internal.ui.http;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.subgraph.vega.ui.http.Activator;


public class ProxyStatusLineContribution extends ContributionItem {
	private CLabel label;
	private Image proxyRunning = Activator.getImageDescriptor("icons/proxy_running.png").createImage();
	private Image proxyStopped = Activator.getImageDescriptor("icons/proxy_stopped.png").createImage();
	
	@Override
	public void fill(Composite parent) {
		new Label(parent, SWT.SEPARATOR);
		label = new CLabel(parent, SWT.SHADOW_NONE);
		setProxyStopped();
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 220;
		label.setLayoutData(layoutData);
	}
	
	public void setProxyRunning(int port) {
		label.setImage(proxyRunning);
		label.setText("Proxy running on port "+ port);
	}
	
	public void setProxyStopped() {
		label.setImage(proxyStopped);
		label.setText("Proxy is not running");
	}

}
