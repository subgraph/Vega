package com.subgraph.vega.impl.scanner.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.model.IScanDirectory;
import com.subgraph.vega.api.scanner.model.IScanHost;
import com.subgraph.vega.api.scanner.model.IScanModel;
import com.subgraph.vega.impl.scanner.ScanCrawlFilter;

public class ScanModel implements IScanModel {
	private final Logger logger = Logger.getLogger("scanner");
	private final ScanCrawlFilter crawlFilter;
	
	private final Set<IScanHost> scanHosts = new LinkedHashSet<IScanHost>();
	private final Set<IScanDirectory> scanDirectories = new LinkedHashSet<IScanDirectory>();
	
	public ScanModel(IScannerConfig config) {
		this.crawlFilter = new ScanCrawlFilter(config.getBaseURI());
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
	
	ScanCrawlFilter getCrawlFilter() {
		return crawlFilter;
	}
	
	public List<IScanHost> getUnscannedHosts() {
		return new ArrayList<IScanHost>(scanHosts);
	}
	
	public List<IScanDirectory> getUnscannedDirectories() {
		return new ArrayList<IScanDirectory>(scanDirectories);
	}

}
