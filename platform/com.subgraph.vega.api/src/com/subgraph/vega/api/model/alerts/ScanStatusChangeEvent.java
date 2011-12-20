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
package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.events.IEvent;

public class ScanStatusChangeEvent implements IEvent {
	private final IScanInstance scanInstance;
	private final int scanStatus;
	private final int scanCompletedCount;
	private final int scanTotalCount;

	public ScanStatusChangeEvent(IScanInstance scanInstance, int status, int completed, int total) {
		this.scanInstance = scanInstance; 
		this.scanStatus = status;
		this.scanCompletedCount = completed;
		this.scanTotalCount = total;
	}
	
	public IScanInstance getScanInstance() {
		return scanInstance;
	}

	public int getStatus() {
		return scanStatus;
	}
	
	public int getCompletedCount() {
		return scanCompletedCount;
	}
	
	public int getTotalCount() {
		return scanTotalCount;
	}
}
