package com.subgraph.vega.api.http.requests;

public interface IPageFingerprint {
	int getCode();
	int[] getData();
	boolean isSame(IPageFingerprint other);
}
