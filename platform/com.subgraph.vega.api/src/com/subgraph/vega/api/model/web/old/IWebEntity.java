package com.subgraph.vega.api.model.web.old;

import java.net.URI;

public interface IWebEntity {
	IWebEntity getParent();
	boolean isVisited();
	URI toURI();
	IWebHost getHostEntity();
	void setVisited(boolean notify);
}
