package com.subgraph.vega.api.scanner.model;

import java.util.List;

import org.w3c.dom.Document;

public interface IScanAlert {
	enum Severity { HIGH, MEDIUM, LOW, INFO, UNKNOWN };
	Severity getSeverity();
	String getTitle();
	void setTemplateName(String name);
	Document getReportXML();
	void setProperty(String name, Object value);
	Object getProperty(String name);
	List<String> propertyKeys();
	String getTemplateName();
}
