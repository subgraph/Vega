package com.subgraph.vega.impl.scanner.handlers;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponse.ResponseStatus;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class DirIPSCheck extends CrawlerModule {
	private final static String IPS_TEST =  
		"?_test1=c:\\windows\\system32\\cmd.exe" +
		"&_test2=/etc/passwd" +
		"&_test3=|/bin/sh" +
		"&_test4=(SELECT * FROM nonexistent) --" +
		"&_test5=>/no/such/file" +
		"&_test6=<script>alert(1)</script>" +
		"&_test7=javascript:alert(1)";
	
	private final static String IPS_SAFE = 
		 "?_test1=ccddeeeimmnossstwwxy.:\\\\\\" +
		  "&_test2=acdepsstw//" +
		  "&_test3=bhins//" +
		  "&_test4=CEEFLMORSTeeinnnosttx-*" +
		  "&_test5=cefhilnosu///" +
		  "&_test6=acceiilpprrrssttt1)(" +
		  "&_test7=aaaceijlprrsttv1):(";

	private final InjectionChecks injection = new InjectionChecks();

	@Override
	public void initialize(IPathState ps) {
		final IModuleContext ctx = ps.createModuleContext();
		ctx.submitRequest(createRequest(ps, IPS_TEST), this, 0);
		ctx.submitRequest(createRequest(ps, IPS_SAFE), this, 1);
	}
	
	private HttpUriRequest createRequest(IPathState ps, String query) {
		final URI baseUri = ps.getPath().getUri();
		try {
			final URI newUri = new URI(baseUri.getScheme(), baseUri.getAuthority(), baseUri.getPath(), query, null);
			return new HttpGet(newUri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void runModule(HttpUriRequest request, IHttpResponse response, IModuleContext ctx) {
		final IPathState ps = ctx.getPathState();
		if(ctx.hasModuleFailed()) 
			return;
		
		if(ctx.getCurrentIndex() == 1 && response.isFetchFail()) {
			ctx.error(request, response, "Fetch failed during IPS tests");
			ctx.setModuleFailed();
			injection.initialize(ps);
		}
		ctx.addRequestResponse(request, response);
		if(!ctx.allResponsesReceived())
			return;
		
		IPathState p404 = ps.get404Parent();
		if(p404 == null || !p404.isIPSDetected()) {
			if(ctx.getSavedResponse(0).getResponseStatus() != IHttpResponse.ResponseStatus.RESPONSE_OK) {
				ctx.debug("Possible IPS filter detected");
				ctx.getPathState().setIPSDetected();
				
			} else if(!ctx.isFingerprintMatch(0, 1)) {
				ctx.debug("Possible IPS filter detected");
				ctx.getPathState().setIPSDetected();
			}
		} else {
			if(ctx.getSavedResponse(0).getResponseStatus() == ResponseStatus.RESPONSE_OK && ctx.isFingerprintMatch(0, 1)) {
				ctx.debug("Previously detected IPS filter is no longer active");
			}
		}
		
		injection.initialize(ps);
	}
}
