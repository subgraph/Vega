package com.subgraph.vega.impl.scanner.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.subgraph.vega.api.annotations.GuardedBy;
import com.subgraph.vega.api.annotations.ThreadSafe;
import com.subgraph.vega.api.scanner.model.IScanAlert;
@ThreadSafe
public class BasicScanAlert implements IScanAlert {
	private final Severity severity;
	private final String title;
	private final Document reportXML;
	private String templateName = "main";
	@GuardedBy("propertyMap")
	private final Map<String, Object> propertyMap = new HashMap<String, Object>();

	BasicScanAlert(String title, Severity severity, Document xmlRoot) {
		this.title = title;
		this.severity = severity;
		this.reportXML = xmlRoot;
	}
	
	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setProperty(String name, Object value) {
		synchronized(propertyMap) {
			propertyMap.put(name, value);
		}
	}

	@Override
	public Object getProperty(String name) {
		synchronized (propertyMap) {
			return propertyMap.get(name);
		}
	}
	
	@Override
	public void setStringProperty(String name, String value) {
		setProperty(name, value);		
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		setProperty(name, value);		
	}

	@Override
	public String getStringProperty(String name) {
		final Object value = getProperty(name);
		if(value == null)
			return null;
		if(value instanceof String)
			return (String) value;
		throw new IllegalArgumentException("Property '"+ name +"' exists but it is not a String");
	}

	@Override
	public Integer getIntegerProperty(String name) {
		final Object value = getProperty(name);
		if(value == null)
			return null;
		if(value instanceof Integer)
			return (Integer) value;
		throw new IllegalArgumentException("Property '"+ name +"' exists but it is not an Integer");
	}
	
	@Override
	public Document getReportXML() {
		return reportXML;
	}
	@Override
	public List<String> propertyKeys() {
		synchronized(propertyMap) {
			return new ArrayList<String>(propertyMap.keySet());
		}
	}

	@Override
	public void setTemplateName(String name) {
		this.templateName = name;		
	}
	
	@Override
	public String getTemplateName() {
		return templateName;
	}
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof BasicScanAlert) {
			final BasicScanAlert that = (BasicScanAlert) other;
			if(!(that.getTitle().equals(this.title) && that.getSeverity().equals(this.severity))) 
				return false;
			if(that.propertyKeys().size() != this.propertyKeys().size()) 
				return false;
			
			for(String k: that.propertyKeys())
				if(!that.getProperty(k).equals(this.getProperty(k)))
					return false;
			return true;
		}
		return false;
	}
}
