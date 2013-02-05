package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.ui.http.Activator;

public class ProxyScanHandler extends AbstractHandler {
	public static final String SCANNER_PLUGIN = "com.subgraph.vega.ui.scanner";
	public static final String P_LOG_ALL_REQUESTS = "LogAllRequests";
	public static final String P_DISPLAY_DEBUG_OUTPUT = "DisplayDebugOutput";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
				
		boolean oldValue = HandlerUtil.toggleCommandState(event.getCommand());
		
		final IHttpProxyService proxyService = Activator.getDefault().getProxyService();
		IScannerConfig scannerConfig = proxyService.getProxyScanConfig();
		if(scannerConfig != null) {
			configureScanner(scannerConfig);
		}
		proxyService.setProxyScanEnabled(!oldValue);
		return null;
	}
	
	private void configureScanner(IScannerConfig config) {
		final IPreferencesService preferencesService = Platform.getPreferencesService();
		final boolean logAll = preferencesService.getBoolean(SCANNER_PLUGIN, P_LOG_ALL_REQUESTS, false, null);
		final boolean displayDebug = preferencesService.getBoolean(SCANNER_PLUGIN, P_DISPLAY_DEBUG_OUTPUT, false, null);
		config.setLogAllRequests(logAll);
		config.setDisplayDebugOutput(displayDebug);
	}
}
