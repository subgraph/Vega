package com.subgraph.vega.api.scanner;

import java.net.URI;

public interface IScanProbeResult {
	enum ProbeResultType { PROBE_OK, PROBE_REDIRECT, PROBE_REDIRECT_FAILED, PROBE_CONNECT_FAILED };
	
	ProbeResultType getProbeResultType();
	URI getRedirectTarget();
	String getFailureMessage();
}
