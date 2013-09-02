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
package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

import com.subgraph.vega.api.analysis.IContentAnalyzer;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.http.requests.IHttpMacroExecutor;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodHttpMacro;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;
import com.subgraph.vega.api.util.VegaURI;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;

public class ScannerTask implements Runnable {
	private final Logger logger = Logger.getLogger("scanner");
	private final Scan scan;
	private final IScanInstance scanInstance;
	private IContentAnalyzer contentAnalyzer;
	private final UriParser uriParser;
	private final UriFilter uriFilter;
	private final ITargetScope scanTargetScope;
	private volatile boolean stopRequested;
	private IWebCrawler currentCrawler;
	
	
	ScannerTask(Scan scan) {
		this.scan = scan;
		this.scanInstance = scan.getScanInstance();
		currentCrawler = scan.getScanner().getWebCrawlerFactory().create(scan.getRequestEngine());
		contentAnalyzer = scan.getScanner().getContentAnalyzerFactory().createContentAnalyzer(scanInstance);
		contentAnalyzer.setResponseProcessingModules(scan.getResponseModules());
		uriFilter = new UriFilter(scan.getConfig());
		uriParser = new UriParser(scan.getConfig(), scan.getBasicModules(), scan.getWorkspace(), currentCrawler, uriFilter, contentAnalyzer, scanInstance, false);
		scanTargetScope = scan.getConfig().getScanTargetScope();

		logger.setLevel(Level.ALL);

//		URI redirectURI = scan.getRedirectURI();

		/* ugly hack */
	/*	
		if (redirectURI != null) && redirectURI.getHost().equals(baseURI.getHost())) 
			uriParser.processUri(redirectURI);
		} else

			for(URI u: scanTargetScope.getScopeURIs()) {
				uriParser.processUri(u);
			}
	*/
	}
	
	void stop() {
		stopRequested = true;
		if(currentCrawler != null)
			try {
				currentCrawler.stop();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public void run() {


		scanInstance.updateScanStatus(IScanInstance.SCAN_AUDITING);
		scan.getRequestEngine().getCookieStore().clear();
		
		if (handleMacroAuthentication()) {
			runCrawlerPhase();

			if(stopRequested) {
				scanInstance.updateScanStatus(IScanInstance.SCAN_CANCELLED);
				logger.info("Scanner cancelled");
			} else {
				scanInstance.updateScanStatus(IScanInstance.SCAN_COMPLETED);
				logger.info("Scanner completed");
			}
		}
		scan.doFinish();
		printModuleRuntimeStats();
	}
	
	private void printModuleRuntimeStats() {
		logger.info("Scanning module runtime statistics:");
		for(IScannerModule m: scan.getResponseModules()) {
			IScannerModuleRunningTime profile = m.getRunningTimeProfile();
			if(profile.getInvocationCount() > 0)
				logger.info(profile.toString());			
		}
		for(IScannerModule m: scan.getBasicModules()) {
			IScannerModuleRunningTime profile = m.getRunningTimeProfile();
			if(profile.getInvocationCount() > 0)
				logger.info(profile.toString());			
		}
	}
	
	// temporary: in the future this will be managed with session handling rules
	private boolean handleMacroAuthentication() {
		IIdentity identity = scan.getConfig().getScanIdentity();
		if (identity != null) {
			IAuthMethod authMethod = identity.getAuthMethod();
			if (authMethod != null && authMethod.getType() == IAuthMethod.AuthMethodType.AUTH_METHOD_HTTP_MACRO) {
				logger.info("Pre-authenticating using an HTTP macro");
				IAuthMethodHttpMacro authMethodMacro = (IAuthMethodHttpMacro)authMethod;
				IHttpMacroContext context = scan.getRequestEngine().createMacroContext();
				context.setDict(identity.getDict());
				IHttpMacroExecutor executor = scan.getRequestEngine().createMacroExecutor(authMethodMacro.getMacro(), context);
				while (executor.hasNext()) {
					IHttpResponse response;
					int status;
					
					try {
						response = executor.sendNextRequest().get(true);
					} catch (Exception e) {
						logger.log(Level.WARNING, e.getMessage());
						return false;
					}		
					contentAnalyzer.processResponse(response, true, false);
					status = response.getResponseCode();
					if (status == 301 || status == 302 || status == 303 || status == 307) {
						Header locationHeader = response.getRawResponse().getFirstHeader("Location");
						if (locationHeader == null) {
							return false;
						}
						else {
							final VegaURI base = VegaURI.fromHostAndRequest(response.getHost(), response.getOriginalRequest());
							final VegaURI uri = base.resolve(locationHeader.getValue());
							if (uriFilter.filter(uri)) {	
								uriParser.processUri(uri);
							}
						}
							
					}
					
					
				}
			}
		}
		return true;
	}
	
	private void runCrawlerPhase() {
		logger.info("Starting crawling phase");
				
		for(URI u: scanTargetScope.getScopeURIs()) {
			uriParser.processUri(toVegaURI(u));
		}
		currentCrawler.start();
		try {
			currentCrawler.waitFinished();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentCrawler = null;
		logger.info("Crawler finished");
	}

	private VegaURI toVegaURI(URI u) {
		final HttpHost targetHost = URIUtils.extractHost(u);
		return new VegaURI(targetHost, u.getPath(), u.getQuery());
	}

	void pauseScan() {
		final IWebCrawler crawler = currentCrawler;
		if(crawler != null) {
			crawler.pause();
			scanInstance.notifyScanPauseState(true);
		}
	}

	void unpauseScan() {
		final IWebCrawler crawler = currentCrawler;
		if(crawler != null) {
			crawler.unpause();
			scanInstance.notifyScanPauseState(false);
		}
	}
	
	boolean isPaused() {
		final IWebCrawler crawler = currentCrawler;
		if(crawler != null) {
			return crawler.isPaused();
		} else {
			return false;
		}
	}
}
