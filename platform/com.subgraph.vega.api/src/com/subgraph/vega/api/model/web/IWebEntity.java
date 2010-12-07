package com.subgraph.vega.api.model.web;

import com.subgraph.vega.api.model.IModelProperties;

public interface IWebEntity extends IModelProperties {
	boolean isVisited();
	void setVisited(boolean notify);
	boolean isScanned();
	void setScanned();
	IWebEntity getParent();

}
