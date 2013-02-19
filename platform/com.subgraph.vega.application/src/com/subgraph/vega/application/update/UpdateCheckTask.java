package com.subgraph.vega.application.update;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.http.requests.IHttpRequestTask;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.RequestEngineException;
import com.subgraph.vega.application.Activator;

public class UpdateCheckTask implements Runnable {

	private final static String REPONSE_UPDATE_AVAILABLE = "UPDATE";
	
	private final int buildNumber;
	
	public UpdateCheckTask(Display display, int buildNumber) {
		this.buildNumber = buildNumber;
	}

	@Override
	public void run() {
		final IHttpRequestEngine requestEngine = createRequestEngine();
		final HttpHost targetHost = createTargetHost();
		final String uri = createUriPath();
		HttpUriRequest request = requestEngine.createGetRequest(targetHost, uri);
		IHttpRequestTask requestTask = requestEngine.sendRequest(request);
		try {
			IHttpResponse response = requestTask.get(true);
			processResponseBody(response.getBodyAsString());
		} catch (RequestEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void processResponseBody(String body) {
		if(!REPONSE_UPDATE_AVAILABLE.equalsIgnoreCase(body)) {
			return;
		}

	}

	private IHttpRequestEngine createRequestEngine() {
		final IHttpRequestEngineFactory requestEngineFactory = Activator.getDefault().getHttpRequestEngineFactoryService();
		final IHttpRequestEngineConfig config = requestEngineFactory.createConfig();
		return requestEngineFactory.createRequestEngine(IHttpRequestEngine.EngineConfigType.CONFIG_SCANNER, config, null);
	}
	
	private HttpHost createTargetHost() {
		return new HttpHost("support.subgraph.com", -1, "https");
	}
	
	private String createUriPath() {
		return "/update-check.php?build="+buildNumber;
	}
}
