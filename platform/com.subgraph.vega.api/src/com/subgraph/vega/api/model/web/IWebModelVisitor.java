package com.subgraph.vega.api.model.web;

public interface IWebModelVisitor {
	void visit(IWebHost host);
	void visit(IWebPath path);
	void visit(IWebResponse response);
}
