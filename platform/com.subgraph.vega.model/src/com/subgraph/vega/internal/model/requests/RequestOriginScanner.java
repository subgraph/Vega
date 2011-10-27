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
import com.subgraph.vega.api.model.requests.IRequestOriginScanner;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class RequestOriginScanner extends RequestOrigin implements IRequestOriginScanner {
	private long scanId;
	
	public RequestOriginScanner(long scanId) {
		super(IRequestOrigin.Origin.ORIGIN_SCANNNER);
		this.scanId = scanId;
	}
	
	@Override
	public long getScanId() {
		activate(ActivationPurpose.READ);
		return scanId;
	}

}
