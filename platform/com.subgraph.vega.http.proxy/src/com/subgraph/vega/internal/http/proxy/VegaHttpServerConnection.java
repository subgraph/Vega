package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.impl.SocketHttpServerConnection;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Specialized HttpServerConnection which overrides createHttpRequestFactor() to return
 * an HttpRequestFactory instance which can process the 'CONNECT' method for supporting
 * SSL interception.
 */
public class VegaHttpServerConnection extends SocketHttpServerConnection {
	private HttpRequest cachedRequest;
	private final HttpParams params;

	private boolean isSSL = false;

	public VegaHttpServerConnection(HttpParams params) {
		this.params = params;
	}

	@Override
	protected HttpRequestFactory createHttpRequestFactory() {
		return new VegaHttpRequestFactory();
	}

	public boolean isSslConnection() {
		return isSSL;
	}

	public void rebindWithSSL(Socket socket) throws IOException {
		isSSL = true;
		bind(socket, params);

	}

	public void bind(final Socket socket, final HttpParams params) throws IOException {
		if (socket == null) {
			throw new IllegalArgumentException("Socket may not be null");
		}
		if (params == null) {
			throw new IllegalArgumentException("HTTP parameters may not be null");
		}

		socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
		socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));

		int linger = HttpConnectionParams.getLinger(params);
		if (linger >= 0) {
			socket.setSoLinger(linger > 0, linger);
		}

		super.bind(socket, params);
	}

	HttpRequest peekRequestHeader() throws HttpException, IOException {
		if(cachedRequest != null) 
			throw new IllegalStateException("A cached peeked request already exists");
		cachedRequest = super.receiveRequestHeader();
		return cachedRequest;
	}

	void dropCachedPeekRequest() {
		if(cachedRequest == null)
			throw new IllegalStateException("Cannot drop cached peek request, because no request is cached.");
		cachedRequest = null;
	}

	@Override
	public HttpRequest receiveRequestHeader() throws HttpException, IOException {
		if(cachedRequest != null) {
			final HttpRequest result = cachedRequest;
			cachedRequest = null;
			return result;
		}
		return super.receiveRequestHeader();
	}

	public Socket getSocket() {
		return super.getSocket();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		if (isOpen()) {
			buffer.append(getRemotePort());
		} else {
			buffer.append("closed");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
