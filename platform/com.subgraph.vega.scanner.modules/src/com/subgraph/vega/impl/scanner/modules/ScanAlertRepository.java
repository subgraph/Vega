package com.subgraph.vega.impl.scanner.modules;

import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlert.Severity;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.xml.IXmlRepository;

public class ScanAlertRepository implements IScanAlertRepository {
	private final Logger logger = Logger.getLogger("xml-repository");
	private IXmlRepository xmlRepository;
	
	public IScanAlert createAlert(String name) {
		final Document doc = xmlRepository.getDocument("alerts/"+ name + ".xml");
		if(doc == null) {
			logger.warning("Could not find XML alert description named "+ name);
			return null;
		}
		
		final Element alert = getAlertElement(doc, name);
		if(alert == null)
			return null;
		
		final Severity severity = parseSeverity(alert);
		final String title = parseTitle(alert);
		
		return new BasicScanAlert(title, severity, doc);
      
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
		NodeList nodeList = alertElement.getElementsByTagName("title");
		if(nodeList.getLength() == 0 || nodeList.item(0).getNodeType() != Node.ELEMENT_NODE)
			return null;
		Element titleElement = (Element) nodeList.item(0);
		return getTextForElement(titleElement);
	}
	
	private String getTextForElement(Element e) {
		NodeList nodes = e.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if(n.getNodeType() == Node.TEXT_NODE) 
				return n.getNodeValue();
		}
		return null;
	}
	
	private Element getAlertElement(Document xmlRoot, String name) {
		final NodeList alertNodes = xmlRoot.getElementsByTagName("alert");
		int n = alertNodes.getLength();
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
	
	protected void setXmlRepository(IXmlRepository xmlRepository) {
		this.xmlRepository = xmlRepository;
	}
	
	protected void unsetXmlRepository(IXmlRepository xmlRepository) {
		this.xmlRepository = null;
	}
	

}
