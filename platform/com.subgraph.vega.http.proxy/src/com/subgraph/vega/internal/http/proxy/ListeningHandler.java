package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

import com.subgraph.vega.internal.http.proxy.ProxyTask.ConnState;

public class ListeningHandler extends AbstractHttpHandler implements NHttpServiceHandler {
	private final HttpResponseFactory responseFactory;
	private final ConnectingIOReactor connectingIOReactor;
	private final HttpInterceptProxy proxy;

	ListeningHandler(HttpInterceptProxy proxy, HttpParams params, HttpProcessor httpProcessor, ConnectionReuseStrategy reuseStrategy, HttpResponseFactory factor, ConnectingIOReactor reactor) {
		super(params, httpProcessor, reuseStrategy);
		this.responseFactory = factor;
		this.connectingIOReactor = reactor;
		this.proxy = proxy;
	}
	
	public void connected(NHttpServerConnection conn) {
		logDebug(conn, "conn open");
		final ProxyTask task = new ProxyTask();
		synchronized(task) {
			processConnected(conn, task);
		}		
	}

	private void processConnected(NHttpServerConnection conn, ProxyTask task) {
		task.setClientIOControl(conn);
		task.setClientState(ConnState.CONNECTED);
		conn.getContext().setAttribute(ProxyTask.ATTRIB, task);
	}
	

	public void requestReceived(NHttpServerConnection conn) {
		logDebug(conn, "request received");
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			try {
				processRequestReceived(conn, task);
			} catch (IOException e) {
				shutdownConnection(conn);	
			} catch (HttpException e) {
				shutdownConnection(conn);	
			} catch (URISyntaxException e) {	
				// TODO Auto-generated catch block
				e.printStackTrace();
				shutdownConnection(conn);
			}
		}
	}
	
	private void processRequestReceived(NHttpServerConnection conn, ProxyTask task) throws IOException, HttpException, URISyntaxException {
		checkPermittedStates(task.getClientState(), ConnState.IDLE, ConnState.CONNECTED);
		final HttpRequest request = conn.getHttpRequest();
		logDebug(conn, ">> "+ request.getRequestLine());
		System.out.println(">> "+ request.getRequestLine());
		final String uri = request.getRequestLine().getUri();
		processURI(uri, task);
		task.setRequest(createClonedRequest(request, task));
		HttpHost hh = task.getHttpHost();
		task.getRequest().setHeader(HTTP.TARGET_HOST, hh.getHostName());
		final InetSocketAddress addr = new InetSocketAddress(hh.getHostName(), hh.getPort());
		task.setTargetAddress(addr.getAddress());
		connectingIOReactor.connect(addr, null, task, null);
		task.setClientState(ConnState.REQUEST_RECEIVED);
		if (request instanceof HttpEntityEnclosingRequest) 
			processExpectContinue(conn, (HttpEntityEnclosingRequest) request);
        else 
        	// No request content expected. Suspend client input
        	conn.suspendInput();
        if(task.getOriginIOControl() != null)
        	task.getOriginIOControl().requestOutput();
	}
	
	private void processURI(String uriLine, ProxyTask task) throws URISyntaxException {
		final URI uri = new URI(uriLine);
		final int uriPort = uri.getPort();
		final int port = (uriPort == -1)?(80):(uriPort);
		final URI pathURI = new URI(null, null, null, -1, uri.getPath(), uri.getQuery(), uri.getFragment());
		final HttpHost httpHost = new HttpHost(uri.getHost(), port, uri.getScheme());
		task.setTarget(httpHost, pathURI.toString());
	}

	private void processExpectContinue(NHttpServerConnection conn, HttpEntityEnclosingRequest request) throws IOException, HttpException {
		if(request.expectContinue()) {
			final HttpResponse ack = responseFactory.newHttpResponse(
                       request.getRequestLine().getProtocolVersion(), 
                       HttpStatus.SC_CONTINUE, 
                       conn.getContext());
			conn.submitResponse(ack);	
		}
		
	}
	
	public void inputReady(NHttpServerConnection conn, ContentDecoder decoder) {
		logDebug(conn, "input ready");
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			checkPermittedStates(task.getClientState(), ConnState.REQUEST_RECEIVED, ConnState.REQUEST_BODY_STREAM);
			try {
				processInput(conn, decoder, task);
			} catch (IOException e) {
				shutdownConnection(conn);
			}
		}
	}
	
	private void processInput(NHttpServerConnection conn, ContentDecoder decoder, ProxyTask task) throws IOException {
		if(readInput(task.getInputBuffer(), task.getRequestBodyBuffer(), decoder, conn, task.getOriginIOControl()))
			inputFinished(conn, task);
		else
			task.setClientState(ConnState.REQUEST_BODY_STREAM);
	}
	
	private void inputFinished(NHttpServerConnection conn, ProxyTask task) {
		task.setClientState(ConnState.REQUEST_BODY_DONE);
		conn.suspendInput();
	}
	
	public void responseReady(NHttpServerConnection conn) {
		logDebug(conn, "response ready", LOG_OUT);
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			try {
				processResponseReady(conn, task);
			} catch (IOException e) {
				shutdownConnection(conn);
			} catch (HttpException e) {
				shutdownConnection(conn);
			}
		}		
	}
	
	private void processResponseReady(NHttpServerConnection conn, ProxyTask task) throws IOException, HttpException {
		final ConnState state = task.getClientState();
		if(state == ConnState.IDLE || state == ConnState.CLOSING)
			return;
		
		checkPermittedStates(state, ConnState.REQUEST_RECEIVED, ConnState.REQUEST_BODY_DONE);
		final HttpRequest request = task.getRequest();
		final HttpResponse response = task.getResponse();
		
		removeMessageHeaders(response);
		
		response.setParams(new DefaultedHttpParams(response.getParams(), httpParams));
		if(task.getOriginState().compareTo(ConnState.CLOSING) >= 0) {
			response.addHeader(HTTP.CONN_DIRECTIVE, "Close");
		}
		final HttpContext ctx = conn.getContext();
		ctx.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		ctx.setAttribute(ExecutionContext.HTTP_REQUEST, request);
		httpProcessor.process(response, ctx);
		conn.submitResponse(response);
		task.setClientState(ConnState.RESPONSE_SENT);
		logDebug(conn, "<< "+ response.getStatusLine(), LOG_OUT);
		
		if(!canResponseHaveBody(request, response)) {
			conn.resetInput();
			if(!reuseStrategy.keepAlive(response, ctx)) {
				logDebug(conn, "close connection", LOG_OUT);
				task.setClientState(ConnState.CLOSING);
				conn.close();
			} else {
				logDebug(conn, "close connection2", LOG_OUT);
				task.setClientState(ConnState.CLOSING);
				conn.close();
			}	
		}
	}
	
	public void outputReady(NHttpServerConnection conn, ContentEncoder encoder) {
		logDebug(conn, "output ready", LOG_OUT);
		
		final ProxyTask task = getProxyTask(conn);
		synchronized(task) {
			checkPermittedStates(task.getClientState(), ConnState.RESPONSE_SENT, ConnState.RESPONSE_BODY_STREAM);
			
			final HttpResponse response = task.getResponse();
			if(response == null)
				throw new IllegalStateException("HTTP response is null");
			
			try {
				final ByteBuffer src = task.getOutputBuffer();
				src.flip();
				final int bytesWritten = encoder.write(src);
				logDebug(conn, bytesWritten +" bytes written", LOG_OUT);
				logDebug(conn, encoder.toString(), LOG_OUT);
				
				src.compact();
				if(src.position() == 0) {
					if(task.getOriginState() == ConnState.RESPONSE_BODY_DONE) {
						encoder.complete();
					} else {
						conn.suspendOutput();
					}
				}
				
				// Update connection state
				if(encoder.isCompleted()) {
					logDebug(conn, "response body sent", LOG_OUT);
					proxy.completeRequest(task);
					task.setClientState(ConnState.RESPONSE_BODY_DONE);
					if(!reuseStrategy.keepAlive(response, conn.getContext())) {
						logDebug(conn, "close connection", LOG_OUT);
						task.setClientState(ConnState.CLOSING);
					} else {
						logDebug(conn, "close connection", LOG_OUT);
						task.setClientState(ConnState.CLOSING);
						conn.close();
					}
				} else {
					task.setClientState(ConnState.RESPONSE_BODY_STREAM);
					task.getOriginIOControl().requestInput();
				}
				
			} catch (IOException e) {
				shutdownConnection(conn);
			}
		}
	}
	
	public void closed(NHttpServerConnection conn) {
		logDebug(conn, "conn closed");
		final ProxyTask task = getProxyTask(conn);
		if(task != null) {
			synchronized(task) {
				task.setClientState(ConnState.CLOSED);
			}
		}		
	}
	public void exception(NHttpServerConnection conn, HttpException httpex) {
		logDebug(conn, "HTTP error: "+ httpex.getMessage());
		if(conn.isResponseSubmitted()) {
			shutdownConnection(conn);
			return;
		}
		final HttpContext context = conn.getContext();
		final HttpResponse response = responseFactory.newHttpResponse(
				HttpVersion.HTTP_1_0, HttpStatus.SC_BAD_REQUEST, context);
		response.setParams(new DefaultedHttpParams(httpParams, response.getParams()));
		response.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
		// Pre-process HTTP request
		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_REQUEST, null);
		
		try {
			httpProcessor.process(response, context);
			conn.submitResponse(response);
			conn.close();	
		} catch (IOException ex) {
			shutdownConnection(conn);
		} catch (HttpException ex) {
			shutdownConnection(conn);
		}
	}
	
	public void exception(NHttpServerConnection conn, IOException ex) {
		shutdownConnection(conn);
		logDebug(conn, "I/O error: "+ ex.getMessage());
	}


	public void timeout(NHttpServerConnection conn) {
		logDebug(conn, "timeout");
		closeConnection(conn);		
	}
	
	protected String getLogLabel(boolean flag) {
		final String arrow = (flag)?("->"):("<-");
		return "[client"+ arrow +"proxy]";
	}
}
