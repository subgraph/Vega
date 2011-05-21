package com.subgraph.vega.internal.ui.http;


import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.ui.http.Activator;

public class ProxyStatusLineContribution extends ContributionItem {
	private final static int BLINK_INTERVAL = 500;
	private final Logger logger = Logger.getLogger("proxy");
	private CLabel label;
	private Image proxyAlert = Activator.getImageDescriptor("icons/proxy_alert.png").createImage();
	private Image proxyRunning = Activator.getImageDescriptor("icons/proxy_running.png").createImage();
	private Image proxyStopped = Activator.getImageDescriptor("icons/proxy_stopped.png").createImage();
	private final Timer alertBlinkTimer = new Timer();
	private TimerTask alertBlinkTask;
	private boolean alertEnabled;
	private String runningMessage;
		
	@Override
	public void fill(Composite parent) {
		new Label(parent, SWT.SEPARATOR);
		label = new CLabel(parent, SWT.SHADOW_NONE);
		setProxyStopped();
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 220;
		label.setLayoutData(layoutData);
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if(alertEnabled) {
					handleAlertClick();
				}
			}
		});
	}

	public void setProxyRunning(int port) {
		runningMessage = "Proxy running on port " + port;
		setLabelImageAndText(proxyRunning, runningMessage);
	}

	public void setProxyPending(int size) {
		setInterceptionQueueSize(size);
	}
	
	public void setProxyStopped() {
		stopAlert();
		setLabelImageAndText(proxyStopped, "Proxy is not running");
	}
	
	private void setInterceptionQueueSize(int size) {
		if(size > 0) {
			if(!alertEnabled) {
				startAlert();
			}
			final String msg = "("+ size + ") messages intercepted";
			setLabelText(msg);
		} else {
			if(alertEnabled) {
				stopAlert();
				setLabelImageAndText(proxyRunning, runningMessage);
			}
		}
	}

	private void startAlert() {
		synchronized (alertBlinkTimer) {
			if(alertBlinkTask == null) {
				alertBlinkTask = createAlertTask();
				alertBlinkTimer.scheduleAtFixedRate(alertBlinkTask, 0, BLINK_INTERVAL);
				alertEnabled = true;
			}
		}
	}
	
	private void stopAlert() {
		synchronized (alertBlinkTimer) {
			alertEnabled = false;
			if(alertBlinkTask != null) {
				alertBlinkTask.cancel();
				alertBlinkTask = null;
				setLabelImageAndText(proxyRunning, runningMessage);
			}
		}
	}
	
	private TimerTask createAlertTask() {
		return new TimerTask() {
			private boolean state;

			@Override
			public void run() {
				state = !state;
				if(state) {
					setLabelImage(proxyAlert);
				} else {
					setLabelImage(proxyStopped);
				}
			}
		};
	}

	private void setLabelImage(Image image) {
		setLabelImageAndText(image, null);
	}
	
	private void setLabelText(String text) {
		setLabelImageAndText(null, text);
	}

	private void setLabelImageAndText(final Image image, final String text) {
		if(label == null || label.isDisposed()) {
			return;
		}
		label.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(image != null) {
					label.setImage(image);
				}
				if(text != null) {
					label.setText(text);
				}
				label.pack(true);
				label.getParent().layout();
			}
		});
	}

	private void handleAlertClick() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView("com.subgraph.vega.views.intercept");
			stopAlert();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to open interception view", e);
		}		
	}
}
