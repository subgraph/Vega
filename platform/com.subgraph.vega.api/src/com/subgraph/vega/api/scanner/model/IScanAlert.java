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
	void setStringProperty(String name, String value);
	void setIntegerProperty(String name, int value);
	Object getProperty(String name);
	String getStringProperty(String name);
	Integer getIntegerProperty(String name);
	List<String> propertyKeys();
	String getTemplateName();
}
