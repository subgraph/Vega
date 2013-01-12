/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner.handlers;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirParentCheck extends CrawlerModule {

	private final DirIPSCheck ipsCheck = new DirIPSCheck();
	
	public void initialize(IPathState ps) {
		if(!ps.has404Fingerprints() || !hasSuitablePath(ps)) {
			ipsCheck.initialize(ps);
			return;
		}
		
		final IInjectionModuleContext ctx = ps.createModuleContext();
		final HttpUriRequest req = createRequest(ps);
		ctx.submitRequest(req, this, 0);
	}
	
	private boolean hasSuitablePath(IPathState ps) {
		final IWebPath parentPath = ps.getPath().getParentPath();
		return(parentPath != null && parentPath.getParentPath() != null);
	}
	
	private HttpUriRequest createRequest(IPathState ps) {
		final IWebPath path = ps.getPath();
		final IWebPath parent = path.getParentPath();
		String basePath = parent.getParentPath().getFullPath();
		String newPath = basePath + "foo/" + path.getPathComponent();
		return ps.getRequestEngine().createGetRequest(path.getHttpHost(), newPath);
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response, IInjectionModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		
		if(response.isFetchFail()) {
			ctx.error(request, response, "Fetch failed during parent directory check");
		} else if(ps.matchesPathFingerprint(response.getPageFingerprint())) {
			ctx.debug("Problem with parent directory behavior");
			ctx.getPathState().setBadParentDirectory();
		}
	
		ipsCheck.initialize(ctx.getPathState());		
	}
}
