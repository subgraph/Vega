package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectionChecks {
	private final int BH_CHECKS = 15;
	
	private final ICrawlerResponseProcessor putProcessor;
	private final ICrawlerResponseProcessor injectProcessor0;
	
	public InjectionChecks() {
		putProcessor = new InjectPutHandler(this);
		injectProcessor0 = new InjectHandler0(this);
	}
	
	public void intitialize(PathState pathState) {
		pathState.debug("Starting injection checks");
		if(pathState.getPath().getPathType() == PathType.PATH_DIRECTORY) {
			HttpUriRequest req = new HttpPut(pathState.getPath().getUri().resolve("PUT-putfile"));
			pathState.submitRequest(req, putProcessor);
		} else {
			initialize2(pathState);
		}
	}
	
	public void initialize2(PathState pathState) {
		pathState.resetMiscData();
		for(int i = 0; i < BH_CHECKS; i++) 
			pathState.submitRequest(injectProcessor0, i);
	}
	
	public void endChecks() {
		
	}
}
