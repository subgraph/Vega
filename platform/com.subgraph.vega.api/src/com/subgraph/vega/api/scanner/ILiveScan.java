package com.subgraph.vega.api.scanner;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;

public interface ILiveScan {
	void scanGetTarget(URI target, List<NameValuePair> parameters);
	void scanPostTarget(URI target, List<NameValuePair> parameters);
	void stop();
}
