package com.subgraph.vega.api.model.alerts;

import com.subgraph.vega.api.model.IModelProperties;

public interface IScanAlert extends IModelProperties {
	enum Severity { HIGH, MEDIUM, LOW, INFO, UNKNOWN };
	String getName();
	Severity getSeverity();
	String getTitle();
	String getResource();
	void setTemplateName(String name);
	String getTemplateName();
}
