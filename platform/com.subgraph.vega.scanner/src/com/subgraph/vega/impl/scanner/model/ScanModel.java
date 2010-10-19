package com.subgraph.vega.impl.scanner.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;

public class ScanModel implements IScanModel {
	private final Logger logger = Logger.getLogger("scanner");
	
	private final IScanAlertRepository alertRepository;
	private final IHTMLParser htmlParser;
	private final EventListenerManager eventManager = new EventListenerManager();
	private final Set<IScanHost> scanHosts = new LinkedHashSet<IScanHost>();
	private final Set<IScanDirectory> scanDirectories = new LinkedHashSet<IScanDirectory>();
	private final List<IScanAlert> scanAlerts = new ArrayList<IScanAlert>();
	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	public ScanModel(IScanAlertRepository alertRepository, IHTMLParser htmlParser) {
		this.alertRepository = alertRepository;
		this.htmlParser = htmlParser;
	}
	
	@Override
	public void addDiscoveredURI(URI uri) {
		System.out.println("Adding to model: "+ uri);
		
		final URI hostURI = createHostURI(uri);
		if(hostURI == null)
			return;
		final IScanHost host = new ScanHost(hostURI);
		scanHosts.add(host);
		addDirectories(host, uri);
	}
	
	@Override
	public List<IScanAlert> getAlerts() {
		synchronized(scanAlerts) {
			return new ArrayList<IScanAlert>(scanAlerts);
		}
	}
	
	@Override
	public void addAlert(IScanAlert alert) {
		logger.info("Adding alert: "+ alert.getTitle());
		synchronized(scanAlerts) {
			scanAlerts.add(alert);
		}
		eventManager.fireEvent(new NewScanAlertEvent(alert));
		
	}
	
	public void addAlertListenerAndPopulate(IEventHandler listener) {
		List<IScanAlert> alerts = getAlerts();
		for(IScanAlert a: alerts)
			listener.handleEvent(new NewScanAlertEvent(a));
		eventManager.addListener(listener);
	}
	
	public void removeAlertListener(IEventHandler listener) {
		eventManager.removeListener(listener);
	}
	
	private URI createHostURI(URI uri) {
		try {
			return new URI(uri.getScheme(), uri.getHost(), null, null);
		} catch (URISyntaxException e) {
			logger.warning("Syntax error creating URI for host "+ uri.getHost());
			return null;
		}
	}
	
	private void addDirectories(IScanHost host, URI uri) {
		final URI hostURI = host.getURI();
		final StringBuilder sb = new StringBuilder();
		sb.append("/");
		scanDirectories.add(new ScanDirectory(host, hostURI.resolve(sb.toString())));
		for(String pathPart : uri.getPath().split("/")) {
			if(!pathPart.isEmpty()) {
				sb.append(pathPart);
				sb.append("/");
				scanDirectories.add(new ScanDirectory(host, hostURI.resolve(sb.toString())));
			}
		}
	}
	
	public List<IScanHost> getUnscannedHosts() {
		return new ArrayList<IScanHost>(scanHosts);
	}
	
	public List<IScanDirectory> getUnscannedDirectories() {
		return new ArrayList<IScanDirectory>(scanDirectories);
	}

	@Override
	public IScanAlert createAlert(String type) {
		return alertRepository.createAlert(type);
	}

	@Override
	public void setProperty(String key, Object value) {
		synchronized(properties) {
			properties.put(key, value);
		}		
	}

	@Override
	public void setStringProperty(String key, String value) {
		setProperty(key, value);		
	}

	@Override
	public void setIntegerProperty(String key, int value) {
		setProperty(key, value);		
	}

	@Override
	public Object getProperty(String key) {
		synchronized(properties) {
			return properties.get(key);
		}
	}
	
	@Override
	public String getStringProperty(String key) {
		final Object value = getProperty(key);
		if(value == null)
			return null;
		if(value instanceof String)
			return (String) value;
		throw new IllegalArgumentException("Property '"+ key +"' exists but it is not a String");
	}

	@Override
	public Integer getIntegerProperty(String key) {
		final Object value = getProperty(key);
		if(value == null)
			return null;
		if(value instanceof Integer)
			return (Integer) value;
		throw new IllegalArgumentException("Property '"+ key +"' exists but it is not an Integer");
	}

	@Override
	public IHTMLParser getHTMLParser() {
		return htmlParser;
	}

}
