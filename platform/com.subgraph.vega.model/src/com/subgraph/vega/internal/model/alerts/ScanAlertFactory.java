/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.model.alerts;

import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subgraph.vega.api.model.alerts.IScanAlert.Severity;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.xml.IXmlRepository;

public class ScanAlertFactory {
	private final Logger logger = Logger.getLogger("alerts");
	private final IXmlRepository xmlRepository;
	
	ScanAlertFactory(IXmlRepository xmlRepository) {
		this.xmlRepository = xmlRepository;
	}
	
	ScanAlert createAlert(String key, String name, IScanInstance scanInstance, long requestId) {
		Document doc = xmlRepository.getDocument("alerts/"+ name +".xml");
		if(doc == null)
			doc = xmlRepository.getDocument("alerts/default.xml");
		if(doc == null) {
			logger.warning("Could not find XML alert description named "+ name);
			return null;
		}
		final Element alert = getAlertElement(doc, name);
		if(alert == null)
			return null;
		final Severity severity = parseSeverity(alert);
		final String title = parseTitle(alert);
		return new ScanAlert(key, name, title, severity, scanInstance, requestId);
	}
	

	private Severity parseSeverity(Element alertElement) {
		NodeList nodeList = alertElement.getElementsByTagName("severity");
		if(nodeList.getLength() == 0 || nodeList.item(0).getNodeType() != Node.ELEMENT_NODE)
			return Severity.UNKNOWN;
		return processSeverityElement((Element) nodeList.item(0));
	}
	
	private Severity processSeverityElement(Element e) {
		final String sev = getTextForElement(e);
		if(sev == null)
			return Severity.UNKNOWN;
		if(sev.equalsIgnoreCase("high"))
			return Severity.HIGH;
		else if(sev.equalsIgnoreCase("medium"))
			return Severity.MEDIUM;
		else if(sev.equalsIgnoreCase("low"))
			return Severity.LOW;
		else if(sev.equalsIgnoreCase("info"))
			return Severity.INFO;
		else
			return Severity.UNKNOWN;
	}

	private String parseTitle(Element alertElement) {
		final NodeList nodeList = alertElement.getElementsByTagName("title");
		if(nodeList.getLength() == 0 || nodeList.item(0).getNodeType() != Node.ELEMENT_NODE)
			return null;
		final Element titleElement = (Element) nodeList.item(0);
		return getTextForElement(titleElement);
	}
	
	private String getTextForElement(Element e) {
		final NodeList nodes = e.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if(n.getNodeType() == Node.TEXT_NODE) 
				return n.getNodeValue();
		}
		return null;
	}
	
	private Element getAlertElement(Document xmlRoot, String name) {
		final NodeList alertNodes = xmlRoot.getElementsByTagName("alert");
		final int n = alertNodes.getLength();
		if(n == 0) {
			logger.warning("No alert node found in XML for description "+ name);
			return null;
		}
		if(n > 1) {
			logger.warning("Multiple alert entries found in XML for description "+ name +", ignoring extras");
		}
		if(alertNodes.item(0).getNodeType() != Node.ELEMENT_NODE)
			return null;
		return (Element) alertNodes.item(0);
	}

}
