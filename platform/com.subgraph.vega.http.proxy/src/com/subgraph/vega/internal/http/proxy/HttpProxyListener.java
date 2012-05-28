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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.ResponseConnControl;

import com.subgraph.vega.api.http.proxy.IHttpProxyListener;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.internal.http.proxy.ssl.SSLContextRepository;

public class HttpProxyListener implements IHttpProxyListener {
	static final String PROXY_CONTEXT_REQUEST = "proxy.request";
	static final String PROXY_CONTEXT_RESPONSE = "proxy.response";
	static final String PROXY_HTTP_HOST = "proxy.host";
	static final String PROXY_HTTP_TRANSACTION = "proxy.transaction";
	private final Logger logger = Logger.getLogger("proxy");
	private IHttpProxyListenerConfig config;
	private final ProxyTransactionManipulator transactionManipulator;
	private final HttpInterceptor interceptor;
	private final IHttpRequestEngine requestEngine;
	private final List<IHttpInterceptProxyEventHandler> eventHandlers;
	private ServerSocket serverSocket;
	private HttpParams params;
	private VegaHttpService httpService;
	private ExecutorService executor;
	private Thread proxyThread;
	private final List<ConnectionTask> connectionList;

	public HttpProxyListener(IHttpProxyListenerConfig config, ProxyTransactionManipulator transactionManipulator, HttpInterceptor interceptor, IHttpRequestEngine requestEngine, SSLContextRepository sslContextRepository) {
		this.config = config;
		this.transactionManipulator = transactionManipulator;
		this.interceptor = interceptor;
		this.requestEngine = requestEngine;
		this.eventHandlers = new ArrayList<IHttpInterceptProxyEventHandler>();

		this.params = new BasicHttpParams();
		this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0)
		.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
//		.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
		.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

		BasicHttpProcessor inProcessor = new BasicHttpProcessor();
		inProcessor.addInterceptor(new ResponseConnControl());
		inProcessor.addInterceptor(new ResponseContentCustom());
		
		HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
		registry.register("*", new ProxyRequestHandler(this, logger, requestEngine));

		httpService = new VegaHttpService(inProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), registry, params, sslContextRepository);
		
		connectionList = new ArrayList<ConnectionTask>();
	}

	@Override
	public IHttpProxyListenerConfig getConfig() {
		return config;
	}

	@Override
	public IHttpRequestEngine getRequestEngine() {
		return requestEngine;
	}

	@Override
	public void start() {
		executor = Executors.newCachedThreadPool(); // REVISIT: potentially sloppy to just recreate this here
		try {
			logger.info("Listening on " + config.getListenerAddress());
			serverSocket = new ServerSocket(config.getPort(), config.getBacklog(), config.getInetAddress());
			proxyThread = new Thread(createProxyLoopRunnable());
			proxyThread.start();
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO error creating listening socket on " + config.getListenerAddress() + ": "+ e.getMessage(), e);
		}
	}

	private Runnable createProxyLoopRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				proxyAcceptLoop();
			}
		};
	}

	private void proxyAcceptLoop() {
		while(!Thread.interrupted()) {
			Socket s;
			try {
				s = serverSocket.accept();
			} catch (IOException e) {
				if (!Thread.interrupted()) {
					logger.log(Level.WARNING, "IO error processing incoming connection: "+ e.getMessage(), e);
				}
				break;
			}

			logger.fine("Connection accepted from "+ s.getRemoteSocketAddress());
			VegaHttpServerConnection c = new VegaHttpServerConnection(params);
			try {
				c.bind(s, params);
			} catch (IOException e) {
				logger.log(Level.WARNING, "Unexpected error: " + e.getMessage(), e);
				continue;
			}

			final ConnectionTask task = new ConnectionTask(httpService, c, HttpProxyListener.this);
			synchronized (connectionList) {
				connectionList.add(task);
			}
			executor.execute(task);
		}

		synchronized (connectionList) {
			for (ConnectionTask task: connectionList) {
				task.shutdown();
			}
		}

		executor.shutdownNow();
	}

	@Override
	public void stop() {
		proxyThread.interrupt();
		try {
			// close the socket to interrupt accept() in proxyAcceptLoop()
			serverSocket.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Unexpected exception closing server socket: " + e.getMessage(), e);
		}
	}

	@Override
	public void registerEventHandler(IHttpInterceptProxyEventHandler handler) {
		synchronized(eventHandlers) {
			eventHandlers.add(handler);
		}
	}

	@Override
	public void unregisterEventHandler(IHttpInterceptProxyEventHandler handler) {
		synchronized(eventHandlers) {
			eventHandlers.remove(handler);
		}
	}

	public boolean handleTransaction(ProxyTransaction transaction) throws InterruptedException {
		if (transaction.hasResponse() == false) {
			transactionManipulator.process(transaction.getRequest());
		} else {
			transactionManipulator.process(transaction.getResponse().getRawResponse());
		}

		boolean rv = interceptor.handleTransaction(transaction);
		if (rv == true) {
			transaction.await();
		}
		return rv;
	}

	void completeRequest(ProxyTransaction transaction) {
		synchronized(eventHandlers) {
			for(IHttpInterceptProxyEventHandler h: eventHandlers)
				h.handleRequest(transaction);
		}
	}

	public void notifyClose(ConnectionTask task) {
		synchronized (connectionList) {
			connectionList.remove(task);
		}
	}
	
}
