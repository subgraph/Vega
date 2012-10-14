package com.subgraph.vega.api.model.web;

public interface IWebModelVisitable {
	void accept(IWebModelVisitor visitor);
}
