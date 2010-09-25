package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

public class HttpRequestEngine implements IHttpRequestEngine {
	private final Logger logger = Logger.getLogger("request-engine");
	private final static int NTHREADS = 12;
	ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
	HttpClient client = HttpClientFactory.createHttpClient();
	HttpRequestEngineConfig config = new HttpRequestEngineConfig();

	@Override
	public HttpResponse sendRequest(HttpUriRequest request, HttpContext context) throws IOException {
		final HttpContext requestContext = (context == null) ? (new BasicHttpContext()) : (context);
		Future<HttpResponse> future = executor.submit(new RequestTask(client, request, requestContext, config));
		try {
			return future.get();
		} catch (InterruptedException e) {
			logger.info("Request "+ request +" was interrupted before completion");
		} catch (ExecutionException e) {
			if(e.getCause() instanceof IOException) 
				throw ((IOException)e.getCause());
			logger.log(Level.WARNING, "Unexpected exception processing request "+ request +" : "+ e.getCause().getMessage(), e.getCause());
		}
		return null;
	}

	@Override
	public HttpResponse sendRequest(HttpUriRequest request)
			throws IOException, ClientProtocolException {
		return sendRequest(request, null);
	}
}
