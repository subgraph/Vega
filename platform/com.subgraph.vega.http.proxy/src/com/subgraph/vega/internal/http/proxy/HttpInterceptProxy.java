package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultClientIOEventDispatch;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import com.subgraph.vega.http.proxy.IHttpInterceptProxy;
import com.subgraph.vega.http.proxy.IHttpInterceptProxyEventHandler;
import com.subgraph.vega.http.proxy.IProxyTransaction;

public class HttpInterceptProxy implements IHttpInterceptProxy {

	private final List<IHttpInterceptProxyEventHandler> eventHandlers;
	

	private final ConnectingIOReactor connectingIOReactor;
	private final ListeningIOReactor listeningIOReactor;
	private final Thread connectingThread;
	private final Thread listeningThread;
	
	public HttpInterceptProxy(int port) {
		eventHandlers = new ArrayList<IHttpInterceptProxyEventHandler>();
		HttpParams params = createHttpParams();
		connectingIOReactor = createConnectingReactor(params);
		listeningIOReactor = createListeningReactor(params);
		listeningIOReactor.listen(new InetSocketAddress(port));
		createConnectingEventDispatch(params);
		connectingThread = createReactorThread(connectingIOReactor, createConnectingEventDispatch(params));
		listeningThread = createReactorThread(listeningIOReactor, createListeningEventDispatch(this, params, connectingIOReactor));
	}
	
	@Override
	public void startProxy() {
		connectingThread.start();
		listeningThread.start();		
	}

	@Override
	public void stopProxy() {
		try {
			listeningIOReactor.shutdown();
			connectingIOReactor.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	void completeRequest(ProxyTask task) {
		if(eventHandlers.isEmpty())
			return;
		
		final IProxyTransaction transaction = taskToTransaction(task);
		
		for(IHttpInterceptProxyEventHandler h: eventHandlers) {
			h.handleRequest(transaction);
		}
	}
	
	private IProxyTransaction taskToTransaction(ProxyTask task) {
		final HttpRequest request = task.getRequest();
		if(request instanceof HttpEntityEnclosingRequest) {
			final HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) request;
			entityEnclosingRequest.setEntity(task.getRequestEntity());
		}
		final HttpResponse response = task.getResponse();
		response.setEntity(task.getResponseEntity());
		
		return new ProxyTransaction(request, response, task.getHttpHost(), task.getTargetAddress());
	}

	@Override
	public void registerEventHandler(IHttpInterceptProxyEventHandler handler) {
		eventHandlers.add(handler);		
	}

	@Override
	public void unregisterEventHandler(IHttpInterceptProxyEventHandler handler) {
		eventHandlers.remove(handler);		
	}
	
	
	private static Thread createReactorThread(final IOReactor reactor, final IOEventDispatch dispatch) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					reactor.execute(dispatch);
				} catch (IOException ex) {
					ex.printStackTrace();
				}				
			}
		});
	}
	
	private static HttpParams createHttpParams() {
		return new BasicHttpParams()
			.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
			.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
			.setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
			.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
			.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");
	}
	
	private static ConnectingIOReactor createConnectingReactor(HttpParams params) {
		try {
			return new DefaultConnectingIOReactor(1, params);
		} catch (IOReactorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static ListeningIOReactor createListeningReactor(HttpParams params) {
		try {
			return new DefaultListeningIOReactor(1, params);
		} catch (IOReactorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private static IOEventDispatch createListeningEventDispatch(HttpInterceptProxy proxy, HttpParams params, ConnectingIOReactor connectingReactor) {
		final BasicHttpProcessor proc = new BasicHttpProcessor();
		proc.addInterceptor(new ResponseDate());
		proc.addInterceptor(new ResponseServer());
		proc.addInterceptor(new ResponseContent());
		proc.addInterceptor(new ResponseConnControl());
		final ConnectionReuseStrategy reuse = new DefaultConnectionReuseStrategy();
		final HttpResponseFactory factory = new DefaultHttpResponseFactory();
		final ListeningHandler handler = new ListeningHandler(proxy, params, proc, reuse, factory, connectingReactor);
		return new DefaultServerIOEventDispatch(handler, params);
	}
	
	private static IOEventDispatch createConnectingEventDispatch(HttpParams params) {
		final BasicHttpProcessor proc = new BasicHttpProcessor();
		proc.addInterceptor(new RequestContent());
		proc.addInterceptor(new RequestTargetHost());
		proc.addInterceptor(new RequestConnControl());
		proc.addInterceptor(new RequestUserAgent());
		proc.addInterceptor(new RequestExpectContinue());
		final ConnectionReuseStrategy reuse = new DefaultConnectionReuseStrategy();
		final ConnectingHandler handler = new ConnectingHandler(params, proc, reuse);
		return new DefaultClientIOEventDispatch(handler, params);
	}

}
