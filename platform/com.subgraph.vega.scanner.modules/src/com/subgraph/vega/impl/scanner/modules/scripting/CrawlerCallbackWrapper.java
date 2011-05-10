package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.methods.HttpUriRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;

public class CrawlerCallbackWrapper implements ICrawlerResponseProcessor {
	private static final Logger logger = Logger.getLogger("scanner");
	private static final long serialVersionUID = 1L;

	private Function callbackFunction;
	
	public CrawlerCallbackWrapper(Function callback) {
		callbackFunction = callback;
	}
	
	@Override
	public void processResponse(IWebCrawler crawler, HttpUriRequest request, IHttpResponse response, Object argument) {
		if(!(argument instanceof IInjectionModuleContext)) {
			
		}
		final IInjectionModuleContext ctx = (IInjectionModuleContext) argument;
		final Scriptable scope = callbackFunction.getParentScope();
		final Object[] arguments = { request, response, new ModuleContextJS(ctx) };
		try {
			final Context cx = Context.enter();
			callbackFunction.call(cx, scope, scope, arguments);
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
			e.printStackTrace();
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}		
	}
}
