package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.NHttpClientHandler;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

import com.subgraph.vega.internal.http.proxy.ProxyTask.ConnState;

public class ConnectingHandler extends AbstractHttpHandler implements NHttpClientHandler {
	ConnectingHandler(HttpParams params, HttpProcessor processor, ConnectionReuseStrategy strategy) {
		super(params, processor, strategy);	
	}
	
	protected String getLogLabel(boolean flag) {
		final String arrow = (flag)?("<-"):("->");
		return "[proxy"+ arrow +"target]";
	}
	
	public void connected(NHttpClientConnection conn, Object attachment) {
		logDebug(conn, "conn open", LOG_OUT);
		final ProxyTask task = (ProxyTask) attachment;
		logDebug(conn, "task id is "+ task.getTaskId());
		logDebug(conn, "state is "+ task.getOriginState());
		if(task.getRequest() != null)
			logDebug(conn, "req: "+ task.getRequest().getRequestLine());
		synchronized(task) {
			checkPermittedStates(task.getOriginState(), ConnState.IDLE);
			task.setOriginIOControl(conn);
			final HttpContext context = conn.getContext();
			context.setAttribute(ProxyTask.ATTRIB, task);
			task.setOriginState(ConnState.CONNECTED);
			if(task.getRequest() != null)
				conn.requestOutput();
		}
	}

	public void requestReady(NHttpClientConnection conn) {
		logDebug(conn, "request ready", LOG_OUT);
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			final ConnState state = task.getOriginState();
			if(state == ConnState.REQUEST_SENT || state == ConnState.REQUEST_BODY_DONE) {
				// Request sent but no response available yet
				return;
			}
			checkPermittedStates(state, ConnState.IDLE, ConnState.CONNECTED);
			
			final HttpContext context = conn.getContext();
			context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
			context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, task.getHttpHost());
			try {
				final HttpRequest newRequest = createClonedRequest(task.getRequest(), task);
				removeMessageHeaders(newRequest);
				newRequest.removeHeaders(HTTP.TARGET_HOST);
				newRequest.setParams(new DefaultedHttpParams(newRequest.getParams(), httpParams));
				
				httpProcessor.process(newRequest, context);
				conn.submitRequest(newRequest);
				task.setOriginState(ConnState.REQUEST_SENT);
				logDebug(conn, ">> "+ newRequest.getRequestLine(), LOG_OUT);
			} catch (HttpException e) {
				shutdownConnection(conn);
			} catch (IOException e) {
				shutdownConnection(conn);
			}	
		}		
	}
	
	public void outputReady(NHttpClientConnection conn, ContentEncoder encoder) {
		logDebug(conn, "output ready", LOG_OUT);
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			checkPermittedStates(task.getOriginState(), ConnState.REQUEST_SENT, ConnState.REQUEST_BODY_STREAM);
			try {
				processOutput(conn, encoder, task.getInputBuffer(), task.getClientState());
			} catch (IOException e) {
				shutdownConnection(conn);
			}
		}
	}
	
	private void processOutput(NHttpClientConnection conn, ContentEncoder encoder, ByteBuffer src, ConnState otherState) throws IOException {		
		final ProxyTask task = getProxyTask(conn);
		src.flip();
		final int bytesWritten = encoder.write(src);
		logDebug(conn, bytesWritten +" bytes written", LOG_OUT);
		logDebug(conn, encoder.toString());
		if(src.position() == 0) {
			if(task.getClientState() == ConnState.REQUEST_BODY_DONE) {
				encoder.complete();
			} else {
				// Input buffer is empty.  Wait until the client fills up the buffer
				conn.suspendOutput();
			}
		}
		if(encoder.isCompleted()) {
			logDebug(conn, "request body sent", LOG_OUT);
			task.setOriginState(ConnState.REQUEST_BODY_DONE);
		} else {
			task.setOriginState(ConnState.REQUEST_BODY_STREAM);
			task.getClientIOControl().requestInput();
		}
	}
	
	public void responseReceived(NHttpClientConnection conn) {
		logDebug(conn, "response received");
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			checkPermittedStates(task.getOriginState(), ConnState.REQUEST_SENT, ConnState.REQUEST_BODY_DONE);
			final HttpResponse response = conn.getHttpResponse();
			final HttpRequest request = task.getRequest();
			final HttpContext context = conn.getContext();
			logDebug(conn, "<< "+ response.getStatusLine());
			final int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode < HttpStatus.SC_OK)
				return;
			task.setResponse(response);
			task.setOriginState(ConnState.RESPONSE_RECEIVED);
			if(!canResponseHaveBody(request, response)) {
				conn.resetInput();
				if(!reuseStrategy.keepAlive(response, context)) {
					logDebug(conn, "close connection");
					task.setOriginState(ConnState.CLOSING);
					try {
						conn.close();
					} catch (IOException e) {
						shutdownConnection(conn);
						return;
					}
				}
			}
			task.getClientIOControl().requestOutput();
		}		
	}

	public void inputReady(NHttpClientConnection conn, ContentDecoder decoder) {
		logDebug(conn, "input ready");
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			checkPermittedStates(task.getOriginState(), ConnState.RESPONSE_RECEIVED, ConnState.RESPONSE_BODY_STREAM);
			try {
				processInput(conn, decoder, task);
			} catch (IOException ex) {
				shutdownConnection(conn);
			}	
		}
	}
	
	private void processInput(NHttpClientConnection conn, ContentDecoder decoder, ProxyTask task) throws IOException {
		if(readInput(task.getOutputBuffer(), task.getResponseBodyBuffer(), decoder, conn, task.getClientIOControl()))
			inputFinished(conn, task);
		else
			task.setOriginState(ConnState.RESPONSE_BODY_STREAM);
	}
	
	private void inputFinished(NHttpClientConnection conn, ProxyTask task) throws IOException {
		task.setOriginState(ConnState.RESPONSE_BODY_DONE);
		if(!reuseStrategy.keepAlive(task.getResponse(), conn.getContext())) {
			logDebug(conn, "close connection");
			task.setOriginState(ConnState.CLOSING);
			conn.close();
		}
		
	}
	public void closed(NHttpClientConnection conn) {
		logDebug(conn, "conn closed", LOG_OUT);
		final ProxyTask task = getProxyTask(conn);
		if(task != null) {
			synchronized(task) {
				task.setOriginState(ConnState.CLOSED);
			}
		}		
	}

	public void exception(NHttpClientConnection conn, IOException ex) {
		shutdownConnection(conn);
		logDebug(conn, "I/O error: "+ ex.getMessage(), LOG_OUT);		
	}

	public void exception(NHttpClientConnection conn, HttpException ex) {
		shutdownConnection(conn);
		logDebug(conn, "HTTP error: "+ ex.getMessage(), LOG_OUT);		
	}

	public void timeout(NHttpClientConnection conn) {
		logDebug(conn, "timeout", LOG_OUT);
		closeConnection(conn);		
	}

}
