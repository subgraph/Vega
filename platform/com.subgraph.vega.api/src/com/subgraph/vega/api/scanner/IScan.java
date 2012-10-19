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
package com.subgraph.vega.api.scanner;

import java.net.URI;
import java.util.List;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public interface IScan {
	/**
	 * Get the configuration for this scan.
	 * @return Scan configuration.
	 */
	IScannerConfig getConfig();

	/**
	 * Get a configurable list of modules for this scan. TODO: integrate this with config
	 * @return Modules for this scan.
	 */
	List<IScannerModule> getModuleList();

	/**
	 * Probe a URI for redirects. Runs in the thread this is invoked from.
	 * @param uri URI to proble.
	 * @return Probe results.
	 */
	IScanProbeResult probeTargetUri(URI uri);
	
	/**
	 * Start the scan of the target specified in the configuration. The scan starts in a new thread.
	 */
	void startScan();

	/**
	 * Stop the scan.
	 */
	void stopScan();
	
	void pauseScan();
	void unpauseScan();
	boolean isPausedScan();
}
