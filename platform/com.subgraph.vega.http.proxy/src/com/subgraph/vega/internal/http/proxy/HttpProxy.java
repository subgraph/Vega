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
import org.apache.http.protocol.ResponseContent;

import com.subgraph.vega.api.http.proxy.IHttpInterceptProxy;
import com.subgraph.vega.api.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.internal.http.proxy.ssl.SSLContextRepository;

public class HttpProxy implements IHttpInterceptProxy {
	static final String PROXY_CONTEXT_REQUEST = "proxy.request";
	static final String PROXY_CONTEXT_RESPONSE = "proxy.response";
	static final String PROXY_HTTP_HOST = "proxy.host";
	static final String PROXY_HTTP_TRANSACTION = "proxy.transaction";

	private final Logger logger = Logger.getLogger("proxy");

	private final ProxyTransactionManipulator transactionManipulator;
	private final HttpInterceptor interceptor;
	private final List<IHttpInterceptProxyEventHandler> eventHandlers;
	private final int listenPort;
	private ServerSocket serverSocket;
	private HttpParams params;
	private VegaHttpService httpService;
	private ExecutorService executor = Executors.newCachedThreadPool();
	private Thread proxyThread;
	
	public HttpProxy(int listenPort, ProxyTransactionManipulator transactionManipulator, HttpInterceptor interceptor, IHttpRequestEngine requestEngine, SSLContextRepository sslContextRepository) {
		this.eventHandlers = new ArrayList<IHttpInterceptProxyEventHandler>();
		this.transactionManipulator = transactionManipulator;
		this.interceptor = interceptor;
		this.listenPort = listenPort;
		this.params = new BasicHttpParams();
		this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 0)
		.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
		.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
		.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

		BasicHttpProcessor inProcessor = new BasicHttpProcessor();
		inProcessor.addInterceptor(new ResponseConnControl());
		inProcessor.addInterceptor(new ResponseContent());

		HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
		registry.register("*", new ProxyRequestHandler(this, requestEngine));

		httpService = new VegaHttpService(inProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), sslContextRepository);
		httpService.setParams(params);
		httpService.setHandlerResolver(registry);
	}

	@Override
	public void startProxy() {
		try {
			logger.info("Listening on port "+ listenPort);
			serverSocket = new ServerSocket(listenPort);
			proxyThread = new Thread(createProxyLoopRunnable());
			proxyThread.start();
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO error creating listening socket: "+ e.getMessage(), e);
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

			executor.execute(new ConnectionTask(httpService, c, HttpProxy.this));
		}

		executor.shutdownNow();
	}

	@Override
	public void stopProxy() {
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
}
