package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.ui.http.Activator;

public class ForwardRequest extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) {
		IHttpInterceptor interceptor = Activator.getDefault().getProxyService().getInterceptor();
		interceptor.forwardPending();
		return null;
	}

}
