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
			final Object[] args = new Object[] { request, response, ctx	};
			Context cx = Context.enter();
			Scriptable instance = module.createInstanceScope(cx);
			final long startTS = System.currentTimeMillis();
			module.runModule(cx, instance, args);
			final long endTS = System.currentTimeMillis();
			runningTime.addTimestamp((int) (endTS - startTS), request.getRequestLine().getUri());
		} catch (WrappedException e) {
			logger.log(Level.WARNING, new RhinoExceptionFormatter("Wrapped exception running module script", e).toString());
		} catch (RhinoException e) {
			logger.warning(new RhinoExceptionFormatter("Exception running module script.", e).toString());
		} finally {
			Context.exit();
		}
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
}
