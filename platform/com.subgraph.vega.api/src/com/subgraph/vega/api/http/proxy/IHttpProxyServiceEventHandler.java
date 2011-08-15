package com.subgraph.vega.api.http.proxy;

public interface IHttpProxyServiceEventHandler {
	/**
	 * Notification that the proxy service started listening.
	 * @param numListeners Number of proxy listeners.
	 */
	void notifyStart(int numListeners);
	
	/**
	 * Notification that the proxy service stopped listening.
	 */
	void notifyStop();
	
	/**
	 * Notification that the proxy service configuration changed.
	 * @param numListeners Number of proxy listeners.
	 */
	void notifyConfigChange(int numListeners);
}
