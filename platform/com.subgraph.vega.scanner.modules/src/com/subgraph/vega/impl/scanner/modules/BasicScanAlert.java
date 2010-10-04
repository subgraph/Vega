package com.subgraph.vega.impl.scanner.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.subgraph.vega.api.scanner.model.IScanAlert;

public class BasicScanAlert implements IScanAlert {
	private final Severity severity;
	private final String title;
	private final Document reportXML;
	private String templateName = "main";
	
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
		propertyMap.put(name, value);		
	}

	@Override
	public Object getProperty(String name) {
		return propertyMap.get(name);
	}
	
	@Override
	public Document getReportXML() {
		return reportXML;
	}
	@Override
	public List<String> propertyKeys() {
		return new ArrayList<String>(propertyMap.keySet());
	}

	@Override
	public void setTemplateName(String name) {
		this.templateName = name;		
	}
	
	@Override
	public String getTemplateName() {
		return templateName;
	}


}
