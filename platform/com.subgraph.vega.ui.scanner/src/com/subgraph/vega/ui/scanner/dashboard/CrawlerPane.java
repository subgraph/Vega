package com.subgraph.vega.ui.scanner.dashboard;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class CrawlerPane extends Composite {
	private final CrawlerProgressPane progressPane;
	private final Label crawlLabel;
	private volatile boolean changed;
	private boolean finished;
	private boolean canceled;
	private int crawlerTotal;
	private int crawlerCompleted;
	private double crawlerPercent;

	CrawlerPane(Composite parent) {
		super(parent, SWT.NONE);

		setLayout(new GridLayout());
		
		progressPane = new CrawlerProgressPane(this, parent.getBackground());
		GridData gd = new GridData(SWT.CENTER, SWT.BOTTOM, true, true);
		gd.widthHint = 200;
		progressPane.setLayoutData(gd);
		
		crawlLabel = new Label(this, SWT.CENTER);
		crawlLabel.setFont(JFaceResources.getBannerFont());
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd.widthHint = 200;
		crawlLabel.setLayoutData(gd);
		crawlLabel.setBackground(parent.getBackground());
	}
	
	
	boolean isCrawlerFinished() {
		return finished;
	}
	
	void setCrawlerStarting() {
		crawlerTotal = 0;
		crawlerCompleted = 0;
		finished = false;
		canceled = false;
		changed = true;
	}
	
	void setCrawlerFinished() {
		finished = true;
		changed = true;
	}
	
	void setCrawlerCancelled() {
		canceled = true;
		changed = true;
	}

	void renderChanges() {
		if(!changed)
			return;
		
		final Display display = getDisplay();
		if(display.isDisposed())
			return;
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				renderProgress();
				renderLabel();	
			}
		});
	}
	
	synchronized void updateCrawlerProgress(int total, int completed) {
		if(total == crawlerTotal && completed == crawlerCompleted)
			return;
		
		crawlerTotal = total;
		crawlerCompleted = completed;
		crawlerPercent = ((double)crawlerCompleted) / ((double)crawlerTotal) * 100.0;
		changed = true;	
	}
	
	synchronized private void renderProgress() {
		if(canceled) {
			progressPane.setLabelText("Crawler canceled.");
			return;
		}
		else if(finished && !canceled) {
			progressPane.setLabelText("Crawler completed.");
			return;
		} else if(canceled) {
			progressPane.setLabelText("Crawler canceled.");
			return;
		} else {
			progressPane.setProgressBarValue((int) crawlerPercent);
		}
	}
	
	synchronized private void renderLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(crawlerCompleted);
		sb.append(" out of ");
		sb.append(crawlerTotal);
		sb.append(" crawled (");
		sb.append(String.format("%.1f%%", crawlerPercent));
		sb.append(")");
		crawlLabel.setText(sb.toString());
	}
	
	
}
