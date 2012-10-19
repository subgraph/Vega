package com.subgraph.vega.internal.crawler;

public class CrawlerPauseLock {
	private boolean isPaused;
	
	public synchronized void pauseCrawler() {
		isPaused = true;
	}
	
	public synchronized void unpauseCrawler() {
		isPaused = false;
		notifyAll();
	}
	
	public synchronized boolean isPaused() {
		return isPaused;
	}
	
	public void checkIfPaused() throws InterruptedException {
		synchronized(this) {
			while(isPaused) {
				wait();
			}
		}
	}
}
