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
package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrappedException;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public class ResponseProcessorScript implements IResponseProcessingModule, IEnableableModule {
	private static final Logger logger = Logger.getLogger("modules");
	
	private final ScriptedModule module;
	private final ScriptedModuleRunningTime runningTime;
	
	private boolean isEnabled;
	
	
	public ResponseProcessorScript(ScriptedModule module, boolean isEnabled, ScriptedModuleRunningTime runningTime) {
		this.module = module;
		this.isEnabled = isEnabled;
		this.runningTime = runningTime;
	}

	public ResponseProcessorScript(ScriptedModule module) {
		this.module = module;
		this.isEnabled = module.isDefaultEnabled();
		this.runningTime = new ScriptedModuleRunningTime(module.getModuleName());
	}

	public ScriptedModule getModule() {
		return module;
	}

	@Override
	public void processResponse(IScanInstance scanInstance, HttpRequest request, IHttpResponse response,
			IWorkspace workspace) {
		final ResponseModuleContext ctx = new ResponseModuleContext(workspace, scanInstance);
		try {
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			final Object[] arguments = new Object[] { request, createResponse(response, cx, instance), ctx };
			final long startTS = System.currentTimeMillis();
			module.runModule(cx, instance, arguments);
			final long endTS = System.currentTimeMillis();
			runningTime.addTimestamp((int) (endTS - startTS), request.getRequestLine().getUri());
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script: "+ module.getModuleName(), e).toString());
		} catch (RhinoException e) {
			e.printStackTrace();
			logger.warning(new RhinoExceptionFormatter("Exception running module script: "+ module.getModuleName(), e).toString());
		} finally {
			Context.exit();
		}
	}

	private Scriptable createResponse(IHttpResponse response, Context cx, Scriptable scope) {
		Object responseOb = Context.javaToJS(response, scope);
		Object[] args = { responseOb };
		return cx.newObject(scope, "Response", args);
	}

	@Override
	public boolean responseCodeFilter(int code) {
		return true;
	}

	@Override
	public boolean mimeTypeFilter(String mimeType) {
	     return mimeType.toLowerCase().matches("^.*?(text|html|script|xml|json).*$");
	}

	@Override
	public String getModuleName() {
		return module.getModuleName();
	}

	@Override
	public String getModuleCategoryName() {
		return module.getCategoryName();
	}

	@Override
	public ScriptedModuleRunningTime getRunningTimeProfile() {
		return runningTime;
	}

	@Override
	public void setEnabled(boolean flag) {
		isEnabled = flag;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public boolean isDifferential() {
		return false;
	}

	@Override
	public boolean isTimeSensitive() {
		return false;
	}
}
