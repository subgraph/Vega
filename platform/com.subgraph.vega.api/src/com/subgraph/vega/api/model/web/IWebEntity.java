package com.subgraph.vega.api.model.web;

import java.net.URI;

public interface IWebEntity {
	IWebEntity getParent();
	boolean isVisited();
	URI toURI();
	IWebHost getHostEntity();
	void setVisited(boolean notify);
}
