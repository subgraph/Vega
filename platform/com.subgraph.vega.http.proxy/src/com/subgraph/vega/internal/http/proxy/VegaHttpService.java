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
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.protocol.HttpService;
import org.apache.http.util.EncodingUtils;

import com.subgraph.vega.internal.http.proxy.ssl.SSLContextRepository;

/**
 * Specialized HttpService which handles SSL connections.
 */
public class VegaHttpService {
	private final static int DEFAULT_SSL_PORT = 443;

	private final HttpResponseFactory responseFactory;
	private final HttpProcessor processor;
	private final HttpService delegatedHttpService;
	private final SSLContextRepository sslContextRepository;
	private final boolean sslEnabled;

	public VegaHttpService(HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpParams params, SSLContextRepository sslContextRepository) {
		this.delegatedHttpService = new HttpService(proc, connStrategy, responseFactory, handlerResolver, params);
		this.sslContextRepository = sslContextRepository;
		this.sslEnabled = (sslContextRepository != null);
		this.responseFactory = responseFactory;
		this.processor = proc;
	}

	public void handleRequest(final VegaHttpServerConnection conn, final HttpContext context) throws IOException, HttpException {
		if(!sslEnabled) {
			delegatedHttpService.handleRequest(conn, context);
			return;
		}

		final HttpRequest peekRequest = conn.peekRequestHeader();

		if(isCertificateDownload(peekRequest)) {
			conn.dropCachedPeekRequest();
			sendCertificateDownloadResponse(conn, context);
		} else if(isConnectMethodRequest(peekRequest)) {
			conn.dropCachedPeekRequest();
			handleConnect(conn, peekRequest, context);
		} else {
			delegatedHttpService.handleRequest(conn, context);
		}
	}

	private void handleConnect(VegaHttpServerConnection conn, HttpRequest request, HttpContext context) throws IOException, HttpException {
		final HttpHost host = createHostForConnectUri(request.getRequestLine().getUri());
		final SSLSocket sslSocket = createSSLSocketForHost(host, conn.getSocket());

		sendResponseOk(conn, context);
		conn.rebindWithSSL(sslSocket, host);

		try {
			sslSocket.startHandshake();
		} catch (SSLHandshakeException e) {
			conn.close();
			return;
		}
		delegatedHttpService.handleRequest(conn, context);
	}

	private HttpHost createHostForConnectUri(String uri) {
		final String[] parts = uri.split(":");
		final String hostname = parts[0].toLowerCase();
		final int port = (parts.length < 2) ? (DEFAULT_SSL_PORT) : (stringToSSLPort(parts[1]));
		return new HttpHost(hostname, port, "https");
	}

	private int stringToSSLPort(String s) {
		if(s == null || s.isEmpty())
			return DEFAULT_SSL_PORT;
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return DEFAULT_SSL_PORT;
		}
	}

	private void sendCertificateDownloadResponse(VegaHttpServerConnection connection, HttpContext context) throws HttpException, IOException {
		final String pem = sslContextRepository.getCaCertificatePem();
		final byte[] body = EncodingUtils.getAsciiBytes(pem);
		ByteArrayEntity entity = new ByteArrayEntity(body);
		entity.setContentType("application/x-x509-ca-cert; charset=US-ASCII");
		sendResponseOk(connection, context, entity);
	}

	private void sendResponseOk(VegaHttpServerConnection connection, HttpContext context) throws HttpException, IOException {
		sendResponseOk(connection, context, null);
	}

	private void sendResponseOk(VegaHttpServerConnection connection, HttpContext context, HttpEntity entity) throws HttpException, IOException {
		final ProtocolVersion version = new ProtocolVersion("HTTP", 1, 0);
		final HttpResponse response = responseFactory.newHttpResponse(version, HttpStatus.SC_OK, context);
		if(entity != null)
			response.setEntity(entity);
		processor.process(response, context);
		connection.sendResponseHeader(response);
		connection.sendResponseEntity(response);
		connection.flush();
	}

	private SSLSocket createSSLSocketForHost(HttpHost host, Socket socket) throws IOException {
		final SSLContext ctx = sslContextRepository.getContextForName(host.getHostName());
		if(ctx == null) {
			throw new IOException("Failed to create SSLContext for host "+ host.getHostName());
		}
		SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();
		SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, host.getHostName(), host.getPort(), true);
		sslSocket.setUseClientMode(false);
		return sslSocket;
	}

	private boolean isConnectMethodRequest(HttpRequest request) {
		final String method = request.getRequestLine().getMethod();
		return (method != null && method.equalsIgnoreCase("CONNECT"));
	}

	private boolean isCertificateDownload(HttpRequest request) {
		return request.getRequestLine().getUri().toLowerCase().equals("http://vega/ca.crt");
	}
}
