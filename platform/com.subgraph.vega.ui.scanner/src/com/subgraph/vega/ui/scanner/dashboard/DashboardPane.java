/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.scanner.dashboard;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledFormText;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.model.alerts.ScanPauseStateChangedEvent;
import com.subgraph.vega.api.model.alerts.ScanStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

public class DashboardPane extends Composite implements IEventHandler {
	private static final RGB GREY_TEXT_COLOR = new RGB(200, 200, 200);
	private final static String VEGA_LOGO = "icons/vega_small.png";

	private static final int UPDATE_INTERVAL = 100;
	private final Display display;
	private final Timer renderTimer = new Timer();
	private TimerTask renderTask;
	private CrawlerPane crawlerPane;
	private AlertPane alertPane;
	private IScanInstance scanInstance;
	private volatile boolean outputRenderNeeded;
	private volatile boolean crawlerRenderNeeded;
	private volatile boolean disableRenderTaskStart;
	private volatile boolean isProgressPaneVisible;
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private ScrolledFormText scrolledFormText;
	
	private int lastStatus;
	private int lastCompletedCount;
	private int lastTotalCount;
	private String lastCurrentPath;
	
	private boolean isProxyInstance;
	
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	
	public DashboardPane(Composite parent) {
		super(parent, SWT.NONE);
		this.display = parent.getDisplay();
		setLayout(new FillLayout());
		toolkit = new FormToolkit(display);
		toolkit.getColors().createColor("grey", GREY_TEXT_COLOR);
		createDashboardForm();
		renderOutput();
		createDisposeListener();
	}

	private void createDisposeListener() {
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				imageCache.dispose();
			}
		});
	}

	public void displayScanInstance(IScanInstance scanInstance) {
		if(this.scanInstance == scanInstance) {
			return;
		}
		cancelRenderTask();
		disableRenderTaskStart = true;
		
		if(this.scanInstance != null) {
			this.scanInstance.removeScanEventListener(this);
		}
		
		this.scanInstance = scanInstance;
		alertPane.reset();
		isProxyInstance = (scanInstance == null) ? (false) : (scanInstance.getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID);
		if(scanInstance != null) {
			this.scanInstance.addScanEventListenerAndPopulate(this);
			lastStatus = 0;
			lastCompletedCount = 0;
			lastTotalCount = 0;
			maybeUpdateCrawler(scanInstance.getScanStatus(), scanInstance.getScanCurrentPath(), scanInstance.getScanCompletedCount(), scanInstance.getScanTotalCount());
			maybeUpdateStatus(scanInstance.getScanStatus());
		}
		final boolean progressPaneVisible = (!isProxyInstance && (scanInstance != null && scanInstance.getScanStatus() == IScanInstance.SCAN_AUDITING));
		setProgressPaneVisible(progressPaneVisible);
		renderOutput();
		disableRenderTaskStart = false;
	}
	
	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	public void reset() {
		alertPane.reset();
		if(scanInstance == null) {
			return;
		}
		for(IScanAlert alert: scanInstance.getAllAlerts()) {
			alertPane.addAlert(alert);
		}
	}

	private void setProgressPaneVisible(final boolean flag) {
		if(isProgressPaneVisible == flag) {
			return;
		}
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				crawlerPane.setVisible(flag);
				isProgressPaneVisible = flag;
				layout();
				crawlerPane.redraw();
				
			}
		});
	}

	private void createDashboardForm() {
		if(scrolledForm != null)
			scrolledForm.dispose();
		
		scrolledForm = createForm(this, toolkit);
		scrolledFormText = createFormText(scrolledForm.getBody(), toolkit);
		FormText formText = scrolledFormText.getFormText();
		
		
		formText.setColor("grey", toolkit.getColors().getColor("grey"));
		formText.setFont("big", JFaceResources.getBannerFont());
		formText.setFont("header", JFaceResources.getHeaderFont());		
		formText.setImage("logo", imageCache.get(VEGA_LOGO));

		crawlerPane = new CrawlerPane(formText);
		
		crawlerPane.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(crawlerPane);
		formText.setControl("crawler", crawlerPane);

		
		alertPane = new AlertPane(formText);
		alertPane.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		toolkit.adapt(alertPane);
		formText.setControl("alerts", alertPane);

		toolkit.paintBordersFor(formText);
		toolkit.decorateFormHeading(scrolledForm.getForm());
		this.layout();
	}
	
	private static ScrolledForm createForm(Composite parent, FormToolkit toolkit) {
		final ScrolledForm form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new FillLayout());
		return form;
	}
	
	private static ScrolledFormText createFormText(Composite parent, FormToolkit toolkit) {
		final ScrolledFormText sftext = new ScrolledFormText(parent, SWT.WRAP | SWT.NO_FOCUS | SWT.V_SCROLL, false);
		final FormText text = toolkit.createFormText(sftext, false);
		sftext.setFormText(text);
		text.marginWidth = 1;
		text.marginHeight = 0;
		text.setHyperlinkSettings(toolkit.getHyperlinkGroup());
		text.setMenu(parent.getMenu());
		text.setWhitespaceNormalized(false);
		toolkit.adapt(sftext);
		return sftext;
	}
	
	
	private void renderOutput() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("<form>");
		addHeader(buffer);
		renderCrawlerSection(buffer);
		renderAlertSummary(buffer);
		addVSpaces(buffer, 2);
		buffer.append("</form>");
		
		if(!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if(!scrolledFormText.isDisposed()) {
						scrolledFormText.setText(buffer.toString());
					}
					outputRenderNeeded = false;
				}
			});
		}
	}
	
	private void renderCrawlerOutput() {
		if(!display.isDisposed()) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if(!crawlerPane.isDisposed()) {
						crawlerPane.renderChanges();
					}
					crawlerRenderNeeded = false;
				}
			});
		}
	}

	private void renderCrawlerSection(StringBuilder sb) {
		if(isProxyInstance) {
			return;
		}
		if(scanInstance == null || scanInstance.getScanStatus() != IScanInstance.SCAN_AUDITING) {
			if(isProgressPaneVisible) {
				setProgressPaneVisible(false);
			}
			return;
		}

		if(!isProgressPaneVisible) {
			setProgressPaneVisible(true);
		}

		if(scanInstance.isScanPaused()) {
			crawlerPane.setScannerPaused(true);
			addIndented(sb, 10, "<span font='header'>Scanner Progress (Scanner Paused)</span>");
		} else {
			crawlerPane.setScannerPaused(false);
			addIndented(sb, 10, "<span font='header'>Scanner Progress</span>");
		}
	
		addVSpaces(sb, 2);
		crawlerPane.renderChanges();
		addIndented(sb, 20, "<control width='500' height='100' href='crawler'/>");
	}
	
	private void renderAlertSummary(StringBuilder sb) {
		addVSpaces(sb, 2);
		final String title = isProxyInstance ? "Proxy Alert Summary" : "Scan Alert Summary";
		addIndented(sb, 10, "<span font='header'>" + title +"</span>");
		addVSpaces(sb, 2);
		addIndented(sb, 20, "<control width='500' href='alerts'/>");
		addVSpaces(sb, 2);
	}
	
	private void addIndented(StringBuilder sb, int indent, String text) {
		sb.append("<p>");
		for(int i = 0; i < indent; i++)
			sb.append(" ");
		sb.append(text);
		sb.append("</p>");
	}
	
	private void addHeader(StringBuilder sb) {
		addVSpaces(sb, 2);
		addIndented(sb, 10, "<img href='logo'/>");
		addVSpaces(sb, 2);
	}
	
	private void addVSpaces(StringBuilder sb, int count) {
		sb.append("<p>");
		for(int i = 0; i < count; i++)
			sb.append("\n");
		sb.append("</p>");
	}
	
	private void processAlert(IScanAlert alert) {
		alertPane.addAlert(alert);
		outputRenderNeeded = true;
	}
	
	private void handleScannerStatusChanged(ScanStatusChangeEvent event) {
		maybeUpdateCrawler(event.getStatus(), event.getCurrentPath(), event.getCompletedCount(), event.getTotalCount());
		maybeUpdateStatus(event.getStatus());
	}
	
	private void maybeUpdateCrawler(int status, String currentPath, int completed, int total) {
		if(currentPath != lastCurrentPath || completed != lastCompletedCount || total != lastTotalCount || status != lastStatus) {
			lastCurrentPath = currentPath;
			lastCompletedCount = completed;
			lastTotalCount = total;
			crawlerPane.updateCrawlerProgress(status, currentPath, total, completed);
			crawlerRenderNeeded = true;
		}
	}

	private void maybeUpdateStatus(int status) {
		if(status == lastStatus) {
			return;
		}
		lastStatus = status;
		outputRenderNeeded = true;
	}
		
	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				if(outputRenderNeeded) {
					renderOutput();
				}
				if(crawlerRenderNeeded) {
					renderCrawlerOutput();
				}
			}			
		};
	}

	private void cancelRenderTask() {
		synchronized(renderTimer) {
			if(renderTask != null) {
				renderTask.cancel();
				renderTask = null;
			}
		}
	}
	
	private void startRenderTask() {
		synchronized(renderTimer) {
			if(renderTask == null) {
				renderTask = createTimerTask();
				renderTimer.scheduleAtFixedRate(renderTask, 0, UPDATE_INTERVAL);
			}
		}
	}

	@Override
	public void handleEvent(IEvent event) {
		synchronized(renderTimer) {
			if(!disableRenderTaskStart && renderTask == null) {
				startRenderTask();
			}
		}

		if (event instanceof ScanStatusChangeEvent) {
			handleScannerStatusChanged((ScanStatusChangeEvent) event);
		} else if (event instanceof NewScanAlertEvent) {
			processAlert(((NewScanAlertEvent)event).getAlert());
		} if(event instanceof ScanPauseStateChangedEvent) {
			outputRenderNeeded = true;
		}
	}
}
