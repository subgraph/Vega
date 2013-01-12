package com.subgraph.vega.api.scanner;

import java.util.List;

import org.apache.http.NameValuePair;

import com.subgraph.vega.api.util.VegaURI;

public interface IProxyScan {
	void scanGetTarget(VegaURI target, List<NameValuePair> parameters);
	void scanPostTarget(VegaURI target, List<NameValuePair> parameters);
	void stop();
}
