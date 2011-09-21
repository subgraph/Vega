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

import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.scanner.modules.IScannerModule;

/**
 * Scanner usage:
 * 	1. set config via setScannerConfig
 *  2. optionally probe one or more targets using probeTargetURI
 *  3. invoke startScanner 
 */
public interface IScanner {
	IScannerConfig createScannerConfig();
	void setScannerConfig(IScannerConfig config);
	IScannerConfig getScannerConfig();
	List<IScannerModule> getAllModules();
	IScanProbeResult probeTargetURI(URI uri);
	void startScanner();
	void stopScanner();
	void runDomTests();
		
	void addLockStatusListener(IEventHandler listener);
	void removeLockStatusListener(IEventHandler listener);
	boolean lock();
	void unlock();
}
