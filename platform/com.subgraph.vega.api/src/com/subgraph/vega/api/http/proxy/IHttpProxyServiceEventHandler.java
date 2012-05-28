package com.subgraph.vega.api.http.proxy;

public interface IHttpProxyServiceEventHandler {
	/**
	 * Notification that the proxy service started listening.
	 * @param numListeners Number of proxy listeners.
	 */
	void notifyStart(int numListeners);

	/**
	 * Notification that a listener started listening.
	 * @param listener The listener that started listening. 
	 */
	void notifyStartListener(IHttpProxyListener listener);
	
	/**
	 * Notification that the proxy service stopped listening.
	 */
	void notifyStop();

	/**
	 * Notification that a listener stopped listening.
	 * @param listener The listener that is no longer listening. 
	 */
	void notifyStopListener(IHttpProxyListener listener);
	
	/**
	 * Notification that the proxy service configuration changed.
	 * @param numListeners Number of proxy listeners.
	 */
	void notifyConfigChange(int numListeners);
}
