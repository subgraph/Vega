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
