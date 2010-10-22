package com.subgraph.vega.ui.scanner.dashboard;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.ICrawlerProgressEvent;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerStatusChangeEvent;
import com.subgraph.vega.ui.scanner.Activator;

public class DashboardPane extends Composite {
	private static final RGB GREY_TEXT_COLOR = new RGB(200, 200, 200);
	private static final int UPDATE_INTERVAL = 300;
	private final Display display;
	private final Timer renderTimer = new Timer();
	private TimerTask renderTask;
	private IScanner.ScannerStatus currentStatus = IScanner.ScannerStatus.SCAN_IDLE;
	private volatile int crawlerTotal;
	private volatile int crawlerCompleted;
	private volatile boolean renderNeeded;
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private FormText formText;
	
	public DashboardPane(Composite parent) {
		super(parent, SWT.NONE);
		this.display = parent.getDisplay();
		Activator.getDefault().getScanner().registerScannerStatusChangeListener(createEventHandler());
		
		setLayout(new FillLayout());
		
		toolkit = new FormToolkit(display);
		toolkit.getColors().createColor("grey", GREY_TEXT_COLOR);
		scrolledForm = createForm(this, toolkit);
		formText = createFormText(scrolledForm.getBody(), toolkit);
		
		
		formText.setColor("grey", toolkit.getColors().getColor("grey"));
		formText.setFont("big", JFaceResources.getBannerFont());
		toolkit.decorateFormHeading(scrolledForm.getForm());
		renderOutput();
		
	}
	
	private static ScrolledForm createForm(Composite parent, FormToolkit toolkit) {
		final ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Scan Information");
		form.getBody().setLayout(new FillLayout());
		return form;
	}
	
	private static FormText createFormText(Composite parent, FormToolkit toolkit) {
		final FormText text = new FormText(parent, SWT.WRAP | SWT.NO_FOCUS);
		text.marginWidth = 1;
		text.marginHeight = 0;
		text.setHyperlinkSettings(toolkit.getHyperlinkGroup());
		text.setMenu(parent.getMenu());
		text.setWhitespaceNormalized(false);
		toolkit.adapt(text, false, false);
		return text;
	}
	
	
	private synchronized void renderOutput() {
		
		final StringBuilder buffer = new StringBuilder();
		buffer.append("<form>");
		switch(currentStatus) {
		case SCAN_IDLE:
			renderIdle(buffer);
			break;
		case SCAN_CRAWLING:
			renderCrawling(buffer);
			break;
		case SCAN_AUDITING:
			renderAuditing(buffer);
			break;
		case SCAN_COMPLETED:
			renderFinished(buffer);
			break;
		case SCAN_STARTING:
			renderStarting(buffer);
			return;
		}
		buffer.append("</form>");
		
		if(!display.isDisposed())
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if(!formText.isDisposed())
						formText.setText(buffer.toString(), true, false);
				}
			});
		}
	
	private void renderIdle(StringBuilder sb) {
		addVSpaces(sb, 2);
		addDefault(sb, "No Scan currently running.");
	}
	
	private void renderStarting(StringBuilder sb) {
		addVSpaces(sb, 2);
		addDefault(sb, "Scanner starting.");
	}
	
	private void renderCrawling(StringBuilder sb) {
		addVSpaces(sb, 2);
		addDefault(sb, "Crawling Stage");
		if(crawlerTotal != 0) {
			addDetail(sb, crawlerCompleted+ " out of "+ crawlerTotal +" crawled ("+ crawlerPercent() + ")");
		}
		addVSpaces(sb, 2);
		addGrey(sb, "Auditing Stage");
		
	}
	
	private String crawlerPercent() {
		return String.format("%.1f%%", ((double)crawlerCompleted) / ((double)crawlerTotal) * 100.0);
	}
	
	private void renderAuditing(StringBuilder sb) {
		addVSpaces(sb, 2);
		addDefault(sb, "Crawling Stage");
		if(crawlerTotal != 0)
			addDetail(sb, crawlerCompleted+ " out of "+ crawlerTotal +" crawled ("+ crawlerPercent() + ")");

		addVSpaces(sb, 2);
		addDefault(sb, "Auditing Stage");
	}
	
	private void renderFinished(StringBuilder sb) {
		addVSpaces(sb, 2);
		addDefault(sb, "Crawling Stage");
		if(crawlerTotal != 0)
			addDetail(sb, crawlerCompleted+ " out of "+ crawlerTotal +" crawled ("+ crawlerPercent() + ")");
		addVSpaces(sb, 2);
		addDefault(sb, "Auditing Stage");
		addVSpaces(sb, 2);
		addDefault(sb, "Scan Completed");
		
	}
	
	private void addDefault(StringBuilder sb, String value) {
		sb.append("<li bindent='20'><span font='big'>");
		sb.append(value);
		sb.append("</span></li>");
	}
	
	private void addGrey(StringBuilder sb, String value) {
		sb.append("<li bindent='20'><span font='big' color='grey'>");
		sb.append(value);
		sb.append("</span></li>");
	}
	
	private void addDetail(StringBuilder sb, String value) {
		addVSpaces(sb, 1);
		sb.append("<p>            <span font='big'>");
		sb.append(value);
		sb.append("</span></p>");
	}
	
	private void addVSpaces(StringBuilder sb, int count) {
		sb.append("<p>");
		for(int i = 0; i < count; i++)
			sb.append("\n");
		sb.append("</p>");
	}
	
	
	private IEventHandler createEventHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof IScannerStatusChangeEvent) {
					handleScannerStatusChanged((IScannerStatusChangeEvent) event);
				} else if(event instanceof ICrawlerProgressEvent) {
					handleCrawlerProgress((ICrawlerProgressEvent) event);
				} else {
					throw new IllegalArgumentException("Unexpected event type received: "+ event);
				}
			}
		};
	}
	
	private void handleScannerStatusChanged(IScannerStatusChangeEvent event) {
		currentStatus = event.getScannerStatus();
		switch(event.getScannerStatus()) {
		case SCAN_IDLE:
			return;
		case SCAN_STARTING:
			renderTask = createTimerTask();
			renderTimer.scheduleAtFixedRate(renderTask, 0, UPDATE_INTERVAL);
			break;
		case SCAN_CRAWLING:			
		case SCAN_AUDITING:
			renderNeeded = true;
			break;
		case SCAN_COMPLETED:
			renderTask.cancel();
			renderOutput();
		}
	}
	
	private void handleCrawlerProgress(ICrawlerProgressEvent event) {
		crawlerTotal = event.getTotalLinkCount();
		crawlerCompleted = event.getCompletedLinkCount();
		renderNeeded = true;
	}
	
	private TimerTask createTimerTask() {
		return new TimerTask() {
			@Override
			public void run() {
				if(renderNeeded)
					renderOutput();
			}			
		};
	}
}
