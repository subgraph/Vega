package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public class ResponseProcessorScript extends AbstractScriptModule implements IResponseProcessingModule {

	public ResponseProcessorScript(ScriptedModule module) {
		super(module);
	}
	
	@Override
	public void processResponse(HttpRequest request, IHttpResponse response,
			IScanModel scanModel) {
		final List<ExportedObject> exports = new ArrayList<ExportedObject>();
		export(exports, "httpRequest", request);
		export(exports, "httpResponse", response);
		export(exports, "scanModel", scanModel);
		runScript(exports);
	}

	@Override
	public boolean responseCodeFilter(int code) {
		return true;
	}

	@Override
	public boolean mimeTypeFilter(String mimeType) {
		return true;
	}

}
