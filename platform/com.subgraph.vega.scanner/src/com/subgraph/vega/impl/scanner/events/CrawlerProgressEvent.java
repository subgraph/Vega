package com.subgraph.vega.impl.scanner.events;

import com.subgraph.vega.api.scanner.ICrawlerProgressEvent;

public class CrawlerProgressEvent implements ICrawlerProgressEvent {

	private final int completed;
	private final int total;
	
	public CrawlerProgressEvent(int completed, int total) {
		this.completed = completed;
		this.total = total;
	}
	
	@Override
	public int getCompletedLinkCount() {
		return completed;
	}

	@Override
	public int getTotalLinkCount() {
		return total;
	}

}
