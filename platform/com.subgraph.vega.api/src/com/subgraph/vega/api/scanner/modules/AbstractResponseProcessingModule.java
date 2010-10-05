package com.subgraph.vega.api.scanner.modules;

public abstract class AbstractResponseProcessingModule implements IResponseProcessingModule {
	
	public boolean responseCodeFilter(int code) {
		return code == 200;
	}
	
	public boolean mimeTypeFilter(String mimeType) {
		return mimeType != null && mimeType.contains("text/html");
	}

}
