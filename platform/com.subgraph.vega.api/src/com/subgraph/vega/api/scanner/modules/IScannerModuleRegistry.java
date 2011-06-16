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
package com.subgraph.vega.api.scanner.modules;

import java.util.List;

public interface IScannerModuleRegistry {
	final static int PROXY_SCAN_ID = -1;
	void runDomTests();
	List<IResponseProcessingModule> getResponseProcessingModules();
	List<IResponseProcessingModule> updateResponseProcessingModules(List<IResponseProcessingModule> currentModules);
	
	List<IBasicModuleScript> getBasicModules();
	List<IBasicModuleScript> updateBasicModules(List<IBasicModuleScript> currentModules);
	
}
