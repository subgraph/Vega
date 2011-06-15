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
package com.subgraph.vega.internal.http.requests;

public class RateLimiter {
	
	private final int msInterval;
	private final Object lock;
	private long lastTimestamp;
	
	RateLimiter(int requestsPerMinute) {
		msInterval = 60000 / requestsPerMinute;
		lock = new Object();
		lastTimestamp = System.currentTimeMillis();
	}
	
	void maybeDelayRequest() throws InterruptedException {
		synchronized(lock) {
			final long elapsed = System.currentTimeMillis() - lastTimestamp;
			if(elapsed < msInterval) {
				Thread.sleep(msInterval - elapsed);
			}
			lastTimestamp = System.currentTimeMillis();
		}
	}
}
