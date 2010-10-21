package com.subgraph.vega.internal.model.scan;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.subgraph.vega.api.annotations.GuardedBy;
import com.subgraph.vega.api.annotations.ThreadSafe;
import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.api.scanner.model.IScanAlertRepository;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;

@ThreadSafe
public class ScanModel implements IScanModel {
	private final Logger logger = Logger.getLogger("scanner");
	
	private final IScanAlertRepository alertRepository;
	private final IHTMLParser htmlParser;
	private final IConsole console;
	private final EventListenerManager eventManager = new EventListenerManager();
	@GuardedBy("scanHosts")
	private final Set<IScanHost> scanHosts = new LinkedHashSet<IScanHost>();
	@GuardedBy("scanHosts")
	private final Set<IScanDirectory> scanDirectories = new LinkedHashSet<IScanDirectory>();
	@GuardedBy("scanAlerts")
	private final List<IScanAlert> scanAlerts = new ArrayList<IScanAlert>();
	@GuardedBy("scanAlerts")
	private final Map<String, List<IScanAlert>> scanAlertsByResource = new HashMap<String, List<IScanAlert>>();
	@GuardedBy("properties")
	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	public ScanModel(IScanAlertRepository alertRepository, IHTMLParser htmlParser, IConsole console) {
		this.alertRepository = alertRepository;
		this.htmlParser = htmlParser;
		this.console = console;
	}
	
	@Override
	public void addDiscoveredURI(URI uri) {		
		final URI hostURI = createHostURI(uri);
		if(hostURI == null)
			return;
		final IScanHost host = new ScanHost(hostURI);
		synchronized(scanHosts) {
			scanHosts.add(host);
			addDirectories(host, uri);
		}
	}
	
	@Override
	public List<IScanAlert> getAlerts() {
		synchronized(scanAlerts) {
			return new ArrayList<IScanAlert>(scanAlerts);
		}
	}
	
	@Override
	public void addAlert(IScanAlert alert) {		
		synchronized(scanAlerts) {
			if(rejectDuplicateAlert(alert))
				return;
			logger.info("Adding alert: "+ alert.getTitle());

			addScanAlertByResource(alert);
			scanAlerts.add(alert);
		}
		eventManager.fireEvent(new NewScanAlertEvent(alert));	
	}
	
	private boolean rejectDuplicateAlert(IScanAlert alert) {
		final String resource = alert.getStringProperty("resource");
		if(resource == null)
			return false;
		final List<IScanAlert> alertList = scanAlertsByResource.get(resource);
		if(alertList == null)
			return false;
		for(IScanAlert a: alertList) {
			if(a.equals(alert))
				return true;
		}
		return false;
	}
	
	private void addScanAlertByResource(IScanAlert alert) {
		final String resource = alert.getStringProperty("resource");
		if(resource == null)
			return;
		getAlertListForResource(resource).add(alert);
	}
	
	private List<IScanAlert> getAlertListForResource(String resource) {
		if(!scanAlertsByResource.containsKey(resource)) {
			scanAlertsByResource.put(resource, new ArrayList<IScanAlert>());
		}
		return scanAlertsByResource.get(resource);
	}
	
	public void addAlertListenerAndPopulate(IEventHandler listener) {
		synchronized(scanAlerts) {
			for(IScanAlert a: getAlerts())
				listener.handleEvent(new NewScanAlertEvent(a));
			eventManager.addListener(listener);
		}
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
		URI pathURI = createPathURI(sb.toString());
		if(pathURI != null)
			scanDirectories.add(new ScanDirectory(host, hostURI.resolve(pathURI)));
		for(String pathPart : uri.getPath().split("/")) {
			if(!pathPart.isEmpty()) {
				sb.append(pathPart);
				sb.append("/");
				pathURI = createPathURI(sb.toString());
				if(pathURI != null)
					scanDirectories.add(new ScanDirectory(host, hostURI.resolve(pathURI)));
			}
		}
	}
	
	private URI createPathURI(String path) {
		try {
			return new URI(path);
		} catch (URISyntaxException e) {
			logger.warning("Failed to create path URI from path: "+ path);
			return null;
		}
	}
	
	public List<IScanHost> getUnscannedHosts() {
		synchronized(scanHosts) {
			return new ArrayList<IScanHost>(scanHosts);
		}
	}
	
	public List<IScanDirectory> getUnscannedDirectories() {
		synchronized(scanHosts) {
			return new ArrayList<IScanDirectory>(scanDirectories);
		}
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

	@Override
	public void consoleWrite(String output) {
		console.write(output);		
	}

	@Override
	public void consoleError(String output) {
		console.error(output);		
	}

}
