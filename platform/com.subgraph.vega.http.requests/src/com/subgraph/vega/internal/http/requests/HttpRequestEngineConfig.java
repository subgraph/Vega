package com.subgraph.vega.internal.http.requests;

public class HttpRequestEngineConfig {

	boolean forceIdentityEncoding() {
		return false;
	}

	boolean decompressGzipEncoding() {
		return true;
	}
}
