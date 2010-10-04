package com.subgraph.vega.impl.scanner.modules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlert.Severity;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;

public class ScanAlertRepository implements IScanAlertRepository {
	private Bundle bundle;
	private DocumentBuilderFactory factory;
	private Map<String, Document> xmlCache = new HashMap<String, Document>();
	
	void activate(BundleContext context) {
		bundle = context.getBundle();
		factory = DocumentBuilderFactory.newInstance();
	}
	
	
	public IScanAlert createAlert(String name) {
		try {
			return readAlert(name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Document createAlertXML(String name) throws IOException, ParserConfigurationException, SAXException {
		if(xmlCache.containsKey(name))
			return xmlCache.get(name);
		URL url = bundle.getEntry("/data/"+ name + ".xml");
		if(url == null) 
			throw new FileNotFoundException("Could not locate alert report named "+ name +".xml");

		final InputStream input = url.openStream();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(input);
		xmlCache.put(name, doc);
		return doc;
	}
	
	private IScanAlert readAlert(String name) throws IOException, ParserConfigurationException, SAXException {
		Document doc = createAlertXML(name);
		Element alert = getAlertElement(doc);
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
	
	private Element getAlertElement(Document xmlRoot) {
		final NodeList alertNodes = xmlRoot.getElementsByTagName("alert");
		int n = alertNodes.getLength();
		if(n == 0) {
			System.out.println("No alert found");
			return null;
		}
		if(n > 1) {
			System.out.println("Multiple alert entries found, ignoring extras");
		}
		if(alertNodes.item(0).getNodeType() != Node.ELEMENT_NODE)
			return null;
		return (Element) alertNodes.item(0);
	}
	

}
