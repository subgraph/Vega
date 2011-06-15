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

public interface IScanProbeResult {
	enum ProbeResultType { PROBE_OK, PROBE_REDIRECT, PROBE_REDIRECT_FAILED, PROBE_CONNECT_FAILED };
	
	ProbeResultType getProbeResultType();
	URI getRedirectTarget();
	String getFailureMessage();
}
