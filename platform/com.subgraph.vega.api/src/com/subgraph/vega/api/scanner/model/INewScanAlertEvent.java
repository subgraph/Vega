package com.subgraph.vega.api.scanner.model;

import com.subgraph.vega.api.events.IEvent;

public interface INewScanAlertEvent extends IEvent {
	IScanAlert getAlert();
}
