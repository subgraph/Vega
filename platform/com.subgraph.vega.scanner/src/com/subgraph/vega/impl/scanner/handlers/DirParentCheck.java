package com.subgraph.vega.impl.scanner.handlers;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
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
		final HttpUriRequest req = createRequest(ps.getPath());
		ctx.submitRequest(req, this, 0);
	}
	
	private boolean hasSuitablePath(IPathState ps) {
		final IWebPath parentPath = ps.getPath().getParentPath();
		return(parentPath != null && parentPath.getParentPath() != null);
	}
	
	private HttpUriRequest createRequest(IWebPath path) {
		final IWebPath parent = path.getParentPath();
		String basePath = parent.getParentPath().getFullPath();
		String newPath = basePath + "foo/" + path.getPathComponent();
		final URI originalUri = path.getUri();
		try {
			final URI newUri =  new URI(originalUri.getScheme(), originalUri.getAuthority(), newPath, null, null);
			return new HttpGet(newUri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
