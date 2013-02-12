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
package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class ConnectionTask implements Runnable {
	private final Logger logger = Logger.getLogger("proxy");

	private final VegaHttpService httpService;
	private final VegaHttpServerConnection connection;
	private final HttpProxyListener proxy;

	ConnectionTask(VegaHttpService httpService, VegaHttpServerConnection connection, HttpProxyListener proxy) {
		this.httpService = httpService;
		this.connection = connection;
		this.proxy = proxy;
	}

	/**
	 * Shutdown the connection by forcing it to close. This will break the connection out from any blocking operations
	 * involving the socket.
	 */
	public synchronized void shutdown() {
		try {
			connection.shutdown();
		} catch (IOException e) {
		}
	}
	
	@Override
	public void run() {
		try {
			processingLoop();
		} catch (ConnectionClosedException e) {
			logger.info("Client closed connection to proxy");
		} catch (IOException e) {
			logger.log(Level.INFO, "IOException processing client request in proxy", e);
		} catch (HttpException e) {
			logger.log(Level.WARNING, "HTTP protocol error processing client request in proxy", e);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unexpected exception processing client request in proxy", e);
		} finally {
			proxy.notifyClose(this);
			if (connection.isOpen()) {
				try {
					connection.shutdown();
				} catch (IOException e) {
				}
			}
		}
	}

	private void processingLoop() throws IOException, HttpException {
		while(!Thread.interrupted() && connection.isOpen()) {
			HttpContext ctx = new BasicHttpContext();
			httpService.handleRequest(connection, ctx);
			processRequestContext(ctx);
		}
	}

	private void processRequestContext(HttpContext context) throws IOException {
		final ProxyTransaction transaction = (ProxyTransaction) context.getAttribute(HttpProxyListener.PROXY_HTTP_TRANSACTION);
		if(transaction != null) {
			proxy.completeRequest(transaction);
		}
	}
}
