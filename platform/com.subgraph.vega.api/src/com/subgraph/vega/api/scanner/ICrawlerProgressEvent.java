package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEvent;

public interface ICrawlerProgressEvent extends IEvent {
	int getCompletedLinkCount();
	int getTotalLinkCount();

}
