package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.ByteArrayBuffer;

import com.subgraph.vega.internal.http.proxy.ProxyTask.ConnState;

abstract class AbstractHttpHandler {
	
	private final Logger logger = Logger.getLogger("proxy");
	protected final static boolean LOG_IN = true;
	protected final static boolean LOG_OUT = false;
	
	protected final HttpParams httpParams;
	protected final HttpProcessor httpProcessor;
	protected final ConnectionReuseStrategy reuseStrategy;
	
	protected AbstractHttpHandler(HttpParams params, HttpProcessor httpProcessor, ConnectionReuseStrategy reuseStrategy) {
		this.httpParams = params;
		this.httpProcessor = httpProcessor;
		this.reuseStrategy = reuseStrategy;
	}
	
	protected void logDebug(NHttpConnection conn, String message) {
		logDebug(conn, message, LOG_IN);
	}
	
	protected void logDebug(NHttpConnection conn, String message, boolean flag) {
		if(!logger.isLoggable(Level.FINE))
			return;
		final ProxyTask task = getProxyTask(conn);
		if(task != null) 
			logger.fine("(task: "+ task.getTaskId() +") "+ conn +" "+ getLogLabel(flag) +" "+ message);
		else
			logger.fine("(task: ?) "+ conn +" "+ getLogLabel(flag) +" "+ message);
	}
	
	abstract protected String getLogLabel(boolean flag);
	
	protected void checkPermittedStates(ConnState currentState, ConnState...allowedStates) {
		for(ConnState st: allowedStates)
			if(currentState == st)
				return;
		throw new IllegalStateException("Illegal connection state: "+ currentState);
	}
	
	protected void removeMessageHeaders(HttpMessage message) {
		if(message == null)
			throw new IllegalArgumentException("HTTP message is null");
		final List<String> headerNames = Arrays.asList(HTTP.CONTENT_LEN, HTTP.TRANSFER_ENCODING, HTTP.CONN_DIRECTIVE,
				"Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers", "Upgrade");
		for(String hdr: headerNames) 
			message.removeHeaders(hdr);
	}
	
	protected boolean canResponseHaveBody(HttpRequest request, HttpResponse response) {
		if(request != null && "HEAD".equalsIgnoreCase(request.getRequestLine().getMethod()))
			return false;
		
		int status = response.getStatusLine().getStatusCode();
		return(status >= HttpStatus.SC_OK
				&& status != HttpStatus.SC_NO_CONTENT
				&& status != HttpStatus.SC_NOT_MODIFIED
				&& status != HttpStatus.SC_RESET_CONTENT);
	}
	
	protected ProxyTask getProxyTask(NHttpConnection conn) {
		return (ProxyTask) conn.getContext().getAttribute(ProxyTask.ATTRIB);
	}
	
	protected boolean readInput(ByteBuffer transferBuffer, ByteArrayBuffer destBuffer, 
			ContentDecoder decoder, IOControl thisIOControl, IOControl sourceIOControl) throws IOException {
		int offset = transferBuffer.position();
		int bytesRead = decoder.read(transferBuffer);
		if(bytesRead > 0)
			destBuffer.append(transferBuffer.array(), offset, bytesRead);
		if(!transferBuffer.hasRemaining())
			thisIOControl.suspendInput();
		if(transferBuffer.position() > 0 && sourceIOControl != null)
			sourceIOControl.requestOutput();
		return decoder.isCompleted();
	}
	
	protected HttpRequest createClonedRequest(HttpRequest originalRequest, ProxyTask task) {
		final String method = originalRequest.getRequestLine().getMethod();
		final String path = task.getTargetPath();
		final HttpRequest newRequest = createNewRequest(originalRequest, method, path);
		newRequest.setParams(originalRequest.getParams());
		newRequest.setHeaders(originalRequest.getAllHeaders());
		return newRequest;
	}
	
	private HttpRequest createNewRequest(HttpRequest originalRequest, String method, String path) {
		if(originalRequest instanceof HttpEntityEnclosingRequest)
			return createEntityEnclosingRequest((HttpEntityEnclosingRequest) originalRequest, method, path);
		else
			return new BasicHttpRequest(method, path);
	}
	
	private HttpRequest createEntityEnclosingRequest(HttpEntityEnclosingRequest originalRequest, String method, String path) {
		final HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(method, path);
		request.setEntity(originalRequest.getEntity());
		return request;
	}
	
	protected void shutdownConnection(NHttpConnection conn) {
		try {
			conn.shutdown();
		} catch (IOException ex) {}
	}
	
	protected void closeConnection(NHttpConnection conn) {
		try {
			conn.close();
		} catch (IOException ex) {}
	}

}
