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

import org.apache.http.client.CookieStore;

import com.subgraph.vega.api.model.IWorkspace;

public interface IScanner {
	IScan createScan();
	IProxyScan createProxyScan(IWorkspace workspace, CookieStore cookieStore);
	void runDomTests();
}
