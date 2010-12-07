package com.subgraph.vega.api.model;

import java.util.List;

public interface IModelProperties {
	void setProperty(String name, Object value);
	void setStringProperty(String name, String value);
	void setIntegerProperty(String name, int value);
	Object getProperty(String name);
	String getStringProperty(String name);
	Integer getIntegerProperty(String name);
	List<String> propertyKeys();
}
