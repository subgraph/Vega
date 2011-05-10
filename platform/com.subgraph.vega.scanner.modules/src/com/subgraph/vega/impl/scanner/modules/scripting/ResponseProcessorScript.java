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
import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;

public class ResponseProcessorScript implements IResponseProcessingModule, IEnableableModule {
	private static final Logger logger = Logger.getLogger("modules");
	
	private final ScriptedModule module;
	
	public ResponseProcessorScript(ScriptedModule module) {
		this.module = module;
	}

	@Override
	public void processResponse(long scanId, HttpRequest request, IHttpResponse response,
			IWorkspace workspace) {
		final ResponseModuleContext ctx = new ResponseModuleContext(workspace, scanId);
		try {
			final Object[] args = new Object[] { request, response, ctx	};
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			module.runModule(cx, instance, args, request.getRequestLine().getUri());
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
		/*
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "httpRequest", request);
		export(exports, "httpResponse", response);
		export(exports, "workspace", workspace);
		runScript(exports, request.getRequestLine().getUri());
		*/
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
	public IScannerModuleRunningTime getRunningTimeProfile() {
		return module.getRunningTime();
	}

	@Override
	public void setEnabled(boolean flag) {
		module.setEnabledState(flag);
	}

	@Override
	public boolean isEnabled() {
		return module.getEnabledState();
	}
}
