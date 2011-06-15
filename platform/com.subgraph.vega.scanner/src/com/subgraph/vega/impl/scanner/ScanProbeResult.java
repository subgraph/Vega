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
package com.subgraph.vega.impl.scanner;

import java.net.URI;

import com.subgraph.vega.api.scanner.IScanProbeResult;

public class ScanProbeResult implements IScanProbeResult {

	static ScanProbeResult createOkResult() {
		return new ScanProbeResult(ProbeResultType.PROBE_OK, null, null);
	}

	static ScanProbeResult createConnectFailedResult(String message) {
		return new ScanProbeResult(ProbeResultType.PROBE_CONNECT_FAILED, message, null);
	}

	static ScanProbeResult createRedirectFailedResult(String message) {
		return new ScanProbeResult(ProbeResultType.PROBE_REDIRECT_FAILED, message, null);
	}

	static ScanProbeResult createRedirectResult(URI redirectURI) {
		return new ScanProbeResult(ProbeResultType.PROBE_REDIRECT, null, redirectURI);
	}

	private final ProbeResultType resultType;
	private final String failureMessage;
	private final URI redirectTarget;
	
	private ScanProbeResult(ProbeResultType resultType, String failureMessage, URI redirectTarget) {
		this.resultType = resultType;
		this.failureMessage = failureMessage;
		this.redirectTarget = redirectTarget;
	}

	@Override
	public ProbeResultType getProbeResultType() {
		return resultType;
	}

	@Override
	public URI getRedirectTarget() {
		return redirectTarget;
	}

	@Override
	public String getFailureMessage() {
		return failureMessage;
	}
}
