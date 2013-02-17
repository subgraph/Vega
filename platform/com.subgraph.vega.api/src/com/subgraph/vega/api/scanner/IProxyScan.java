package com.subgraph.vega.api.scanner;

import java.util.List;

import org.apache.http.NameValuePair;

import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.util.VegaURI;

public interface IProxyScan {
	/**
	 * Get the configuration for this scan.
	 * @return Scan configuration.
	 */
	IScannerConfig getConfig();
	void reloadModules();
	List<IBasicModuleScript> getInjectionModules();
	void scanGetTarget(VegaURI target, List<NameValuePair> parameters);
	void scanPostTarget(VegaURI target, List<NameValuePair> parameters);
	void stop();
}
