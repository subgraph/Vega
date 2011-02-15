package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.SocketHttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

public class ConnectionTask implements Runnable {
	private final Logger logger = Logger.getLogger("proxy");

	private final HttpService httpService;
	private final SocketHttpServerConnection connection;
	private final HttpProxy proxy;

	ConnectionTask(HttpService httpService, SocketHttpServerConnection connection, HttpProxy proxy) {
		this.httpService = httpService;
		this.connection = connection;
		this.proxy = proxy;
	}

	@Override
	public void run() {
		try {
			processingLoop();
		} catch (ConnectionClosedException e) {
			logger.info("Client closed connection to proxy");
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOException processing client request in proxy", e);
		} catch (HttpException e) {
			logger.log(Level.WARNING, "HTTP protocol error processing client request in proxy", e);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Unexpected exception processing client request in proxy", e);
		} finally {
			try {
				connection.shutdown();
			} catch (IOException e) { }
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
		final ProxyTransaction transaction = (ProxyTransaction) context.getAttribute(HttpProxy.PROXY_HTTP_TRANSACTION);

		// REVISIT: verify request, response, host are != null?
		proxy.completeRequest(transaction);
	}
}
