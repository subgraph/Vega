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
package com.subgraph.vega.internal.model.requests;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestOriginScanner;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class RequestOriginScanner extends RequestOrigin implements IRequestOriginScanner {
	private IScanInstance scanInstance;
	
	public RequestOriginScanner(IScanInstance scanInstance) {
		super(IRequestOrigin.Origin.ORIGIN_SCANNNER);
		this.scanInstance = scanInstance;
	}
	
	@Override
	public IScanInstance getScanInstance() {
		activate(ActivationPurpose.READ);
		return scanInstance;
	}

	@Override
	public String toString() {
		return "Scan ID " + scanInstance.getScanId();
	}

}
