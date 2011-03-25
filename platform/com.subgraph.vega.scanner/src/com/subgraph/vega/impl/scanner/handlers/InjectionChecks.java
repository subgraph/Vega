package com.subgraph.vega.impl.scanner.handlers;

import java.util.List;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.model.web.IWebPath.PathType;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.impl.scanner.state.PathState;

public class InjectionChecks {
	private final int BH_CHECKS = 15;
	
	private final ICrawlerResponseProcessor putProcessor;
	
	public InjectionChecks() {
		putProcessor = new InjectPutHandler(this);
	}
	
	public void intitialize(IPathState pathState) {
		final IModuleContext ctx = pathState.createModuleContext();
		//pathState.debug("Starting injection checks");
		if(pathState.getPath().getPathType() == PathType.PATH_DIRECTORY) {
			HttpUriRequest req = new HttpPut(pathState.getPath().getUri().resolve("PUT-putfile"));
			ctx.submitRequest(req, putProcessor);
		} else {
			initialize2(pathState);
		}
	}
	
	public void initialize2(IPathState pathState) {
		List<IBasicModuleScript> crawlerModules = pathState.getModuleRegistry().getBasicModules(true);
		for(IBasicModuleScript m: crawlerModules)
			m.runScript(pathState);
		/*
		for(int i = 0; i < BH_CHECKS; i++) 
			pathState.submitRequest(injectProcessor0, i);
			*/
	}
	
	public void endChecks() {
		
	}
}
