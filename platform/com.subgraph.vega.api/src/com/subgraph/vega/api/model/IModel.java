package com.subgraph.vega.api.model;


public interface IModel {
	boolean openWorkspace(String path);
	IWorkspace getCurrentWorkspace();
}
