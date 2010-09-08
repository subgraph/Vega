package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.nio.IOControl;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

public class ProxyTask {
	public final static String ATTRIB = "vega.proxy-task";
	
	enum ConnState {
		IDLE, CONNECTED, 
		REQUEST_RECEIVED, REQUEST_SENT, REQUEST_BODY_STREAM, REQUEST_BODY_DONE, 
		RESPONSE_RECEIVED, RESPONSE_SENT, RESPONSE_BODY_STREAM, RESPONSE_BODY_DONE, 
		CLOSING, CLOSED
	}
	
	private static Object idLock = new Object();
	private static int nextId = 0;
	private static int getNextTaskId() {
		synchronized(idLock) {
			int ret = nextId;
			nextId++;
			return ret;
		}
	}
	
	private final ByteBuffer inBuffer = ByteBuffer.allocate(10240);
	private final ByteBuffer outBuffer = ByteBuffer.allocate(10240);
	private final ByteArrayBuffer requestBodyBuffer = new ByteArrayBuffer(1024);
	private final ByteArrayBuffer responseBodyBuffer = new ByteArrayBuffer(1024);
	private final int taskId = getNextTaskId();
	
	private ConnState originState = ConnState.IDLE;
	private ConnState clientState = ConnState.IDLE;
	
	private IOControl originIOControl = null;
	private IOControl clientIOControl = null;
	private HttpRequest request = null;
	private HttpResponse response = null;
	private HttpHost httpHost = null;
	private InetAddress targetAddress = null;

	private String targetPath = null;
	
	int getTaskId() { return taskId; }
	ConnState getOriginState() { return originState; }
	ConnState getClientState() { return clientState; }
	HttpHost getHttpHost() { return httpHost; }
	InetAddress getTargetAddress() { return targetAddress; }
	String getTargetPath() { return targetPath; }
	HttpRequest getRequest() { return request; }
	HttpResponse getResponse() { return response; }
	IOControl getClientIOControl() { return clientIOControl; }
	IOControl getOriginIOControl() { return originIOControl; }
	ByteBuffer getInputBuffer() { return inBuffer; }
	ByteBuffer getOutputBuffer() { return outBuffer; }
	ByteArrayBuffer getRequestBodyBuffer() { return requestBodyBuffer; }
	ByteArrayBuffer getResponseBodyBuffer() { return responseBodyBuffer; }
	
	void setTargetAddress(InetAddress address) { targetAddress = address; }
	void setClientIOControl(IOControl io) { clientIOControl = io; }
	void setOriginIOControl(IOControl io) { originIOControl = io; }
	void setClientState(ConnState st) { clientState = st; }
	void setOriginState(ConnState st) { originState = st; }
	void setRequest(HttpRequest request) { this.request = request; }
	void setResponse(HttpResponse response) { this.response = response; }
	
	void appendRequestBody(byte[] data, int offset, int length) {
		requestBodyBuffer.append(data, offset, length);
	}
	
	void appendResponseBody(byte[] data, int offset, int length) {
		responseBodyBuffer.append(data, offset, length);
	}
	
	byte[] getRequestBody() {
		return requestBodyBuffer.toByteArray();
	}
	
	byte[] getResponseBody() {
		return responseBodyBuffer.toByteArray();
	}
	
	HttpEntity getRequestEntity() {
		return bufferToEntity(requestBodyBuffer, request);
	}
	
	HttpEntity getResponseEntity() {
		return bufferToEntity(responseBodyBuffer, response);
	}
	
	private HttpEntity bufferToEntity(ByteArrayBuffer buffer, HttpMessage message) {
		if(buffer.length() > 0)
			return createByteArrayEntity(buffer.toByteArray(), message);
		else
			return null;
	}
	
	private HttpEntity createByteArrayEntity(byte[] bytes, HttpMessage message) {
		final ByteArrayEntity entity = new ByteArrayEntity(bytes);
		if(message.containsHeader(HTTP.CONTENT_TYPE))
			entity.setContentType(message.getFirstHeader(HTTP.CONTENT_TYPE));
		if(message.containsHeader(HTTP.CONTENT_ENCODING))
			entity.setContentEncoding(message.getFirstHeader(HTTP.CONTENT_ENCODING));
		return entity;
	}
	
	void setTarget(HttpHost httpHost, String path) {
		this.httpHost = httpHost;
		targetPath = path;
	}
	
	void reset() {
		inBuffer.clear();
		outBuffer.clear();
		requestBodyBuffer.clear();
		responseBodyBuffer.clear();
		originState = ConnState.IDLE;
		clientState = ConnState.IDLE;
		request = null;
		response = null;
	}
	
	void shutdown() {
		shutdownIOControl(clientIOControl);
		shutdownIOControl(originIOControl);
	}

	private void shutdownIOControl(IOControl io) {
		try {
			if(io != null)
				io.shutdown();
		} catch (IOException e) { }
	}
}
