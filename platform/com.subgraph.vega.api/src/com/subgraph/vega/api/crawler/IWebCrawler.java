package com.subgraph.vega.api.crawler;

public interface IWebCrawler {
	void start();
	void stop() throws InterruptedException;
	void waitFinished() throws InterruptedException;
}
