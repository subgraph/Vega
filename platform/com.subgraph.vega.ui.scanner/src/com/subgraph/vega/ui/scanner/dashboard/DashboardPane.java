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
import org.eclipse.ui.forms.widgets.ScrolledFormText;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.ICrawlerProgressEvent;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScanner.ScannerStatus;
import com.subgraph.vega.api.scanner.IScannerStatusChangeEvent;
import com.subgraph.vega.api.scanner.model.INewScanAlertEvent;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class DashboardPane extends Composite {
	private static final RGB GREY_TEXT_COLOR = new RGB(200, 200, 200);
	private final static String VEGA_LOGO = "icons/vega_small.png";

	private static final int UPDATE_INTERVAL = 100;
	private final Display display;
	private final Timer renderTimer = new Timer();
	private TimerTask renderTask;
	private CrawlerPane crawlerPane;
	private AlertPane alertPane;
	private ScannerStatus currentStatus;
	
	private volatile boolean renderNeeded;
	private FormToolkit toolkit;
	private ScrolledForm scrolledForm;
	private ScrolledFormText scrolledFormText;
	
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	
	public DashboardPane(Composite parent) {
		super(parent, SWT.NONE);
		this.display = parent.getDisplay();
		final IScanner scanner = Activator.getDefault().getScanner();
		scanner.registerScannerStatusChangeListener(createEventHandler());
		currentStatus = scanner.getScannerStatus();
		scanner.getScanModel().addAlertListenerAndPopulate(createAlertHandler());
		
		setLayout(new FillLayout());
		
		toolkit = new FormToolkit(display);
		toolkit.getColors().createColor("grey", GREY_TEXT_COLOR);
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
		
		renderOutput();
		
	}
	
	private static ScrolledForm createForm(Composite parent, FormToolkit toolkit) {
		final ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Scan Information");
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
	
	
	private synchronized void renderOutput() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("<form>");
		addHeader(buffer);
		renderAlertSummary(buffer);
		switch(currentStatus) {
		case SCAN_IDLE:
			renderIdleState(buffer);
			break;
		case SCAN_CRAWLING:
			renderCrawlingState(buffer);
			break;
		case SCAN_AUDITING:
			renderAuditingState(buffer);
			break;
		case SCAN_COMPLETED:
		case SCAN_CANCELED:
			renderFinishedState(buffer);
			break;
		case SCAN_STARTING:
			renderStartingState(buffer);
			return;
		}
		buffer.append("</form>");
		
		if(!display.isDisposed())
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					if(!scrolledFormText.isDisposed())
						scrolledFormText.setText(buffer.toString());
					crawlerPane.layout(true);
				}
			});
		}
	
	private void renderIdleState(StringBuilder sb) {
		addDefault(sb, "No Scan currently running.");
	}
	
	private void renderStartingState(StringBuilder sb) {
		addDefault(sb, "Scanner starting.");
	}
	
	private void renderCrawlingState(StringBuilder sb) {
		renderCrawlerSection(sb);
		addVSpaces(sb, 2);
		addGrey(sb, "Auditing Stage");	
	}
	
	private void renderAuditingState(StringBuilder sb) {
		renderCrawlerSection(sb);
		addVSpaces(sb, 2);
		addDefault(sb, "Auditing Stage");
	}
	
	private void renderFinishedState(StringBuilder sb) {
		renderCrawlerSection(sb);
		addVSpaces(sb, 2);
		addDefault(sb, "Auditing Stage");
		addVSpaces(sb, 2);
		if(currentStatus == ScannerStatus.SCAN_CANCELED)
			addDefault(sb, "Scan Cancelled");
		else
			addDefault(sb, "Scan Completed");
	}
	
	private void renderCrawlerSection(StringBuilder sb) {
		addDefault(sb, "Crawling Stage");
		addVSpaces(sb, 2);
		crawlerPane.renderChanges();
		addIndented(sb, 20, "<control width='350' height='80' href='crawler'/>");
	}
	
	private void renderAlertSummary(StringBuilder sb) {
		addVSpaces(sb, 2);
		addIndented(sb, 10, "<span font='header'>Scan Alert Summary</span>");
		addVSpaces(sb, 2);
		addIndented(sb, 20, "<control width='350' href='alerts'/>");
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
	
	private void addVSpaces(StringBuilder sb, int count) {
		sb.append("<p>");
		for(int i = 0; i < count; i++)
			sb.append("\n");
		sb.append("</p>");
	}
	
	private IEventHandler createAlertHandler() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof INewScanAlertEvent) {
					processAlert(((INewScanAlertEvent)event).getAlert());
				}
			}
		};
	}
	
	private void processAlert(IScanAlert alert) {
		alertPane.addAlert(alert);
		renderNeeded = true;
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
		final ScannerStatus lastStatus = currentStatus;
		currentStatus = event.getScannerStatus();

		switch(currentStatus) {
		case SCAN_IDLE:
			return;
		case SCAN_STARTING:
			renderTask = createTimerTask();
			renderTimer.scheduleAtFixedRate(renderTask, 0, UPDATE_INTERVAL);
			break;
		case SCAN_CRAWLING:
			crawlerPane.setCrawlerStarting();
			renderNeeded = true;
			break;
		case SCAN_AUDITING:
			if(lastStatus == ScannerStatus.SCAN_CRAWLING)
				crawlerPane.setCrawlerFinished();
			renderNeeded = true;
			break;
			
		case SCAN_CANCELED:
			if(lastStatus == ScannerStatus.SCAN_CRAWLING && !crawlerPane.isCrawlerFinished()) 
				crawlerPane.setCrawlerCancelled();
			renderTask.cancel();
			renderOutput();
			break;
		case SCAN_COMPLETED:
			renderTask.cancel();
			renderOutput();
			break;
		}
	}
	
	private void handleCrawlerProgress(ICrawlerProgressEvent event) {
		crawlerPane.updateCrawlerProgress(event.getTotalLinkCount(), event.getCompletedLinkCount());
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
