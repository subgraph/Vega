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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineConfig;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.requests.IRequestOriginScanner;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.scanner.IScanProbeResult;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.modules.IBasicModuleScript;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

public class Scan implements IScan {
	private final Scanner scanner;
	private final IEventHandler workspaceListener;
	private final IScannerConfig config;
	private IScanInstance scanInstance; // guarded by this
	private IWorkspace workspace; // guarded by this
	private ScanProbe scanProbe; // guarded by this
	private IHttpRequestEngine requestEngine; // guarded by this
	private ScannerTask scannerTask; // guarded by this
	private Thread scannerThread; // guarded by this
	private URI redirectURI = null;
	private List<IResponseProcessingModule> responseProcessingModules;
	private List<IBasicModuleScript> basicModules;

	/**
	 * Instantiate a scan.
	 * @param scanner Scanner the scan will be run with.
	 * @param workspace Workspace to be used for the scan.
	 * @return Scan.
	 */
	public static Scan createScan(Scanner scanner, IWorkspace workspace) {
		if (workspace == null) {
			return null;
		}
		final Scan scan = new Scan(scanner);
		scan.setWorkspace(workspace);
		scan.reloadModules();
		return scan;
	}

	private Scan(Scanner scanner) {
		this.scanner = scanner;
		workspaceListener = new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if (event instanceof WorkspaceCloseEvent || event instanceof WorkspaceResetEvent) {
					handleWorkspaceCloseOrReset();
				}
			}
		};
		config = new ScannerConfig();
	}

	private void setWorkspace(IWorkspace workspace) {
		synchronized(this) {
			workspace.lock();
			this.workspace = workspace;
			workspace.getModel().addWorkspaceListener(workspaceListener);
			scanInstance = workspace.getScanAlertRepository().createNewScanInstance();
			scanInstance.setScan(this);
		}
	}

	private void handleWorkspaceCloseOrReset() {
		synchronized(this) {
			scanInstance = null;
			workspace.getModel().removeWorkspaceListener(workspaceListener);
			workspace = null;
		}
	}
	
	@Override
	public IScannerConfig getConfig() {
		return config;
	}

	@Override
	public List<IScannerModule> getModuleList() {
		synchronized(this) {
			reloadModules();
			final List<IScannerModule> moduleList = new ArrayList<IScannerModule>();
			moduleList.addAll(responseProcessingModules);
			moduleList.addAll(basicModules);
			return moduleList;
		}
	}

	@Override
	public IScanProbeResult probeTargetUri(URI uri) {
		synchronized(this) {
			if (scanInstance == null) {
				throw new IllegalStateException("Scan is detached from workspace; scan instance was lost. A new scan must be created");
			}

			synchronized(scanInstance) {
				final int scanStatus = scanInstance.getScanStatus();
				if (scanStatus != IScanInstance.SCAN_CONFIG) {
					if (scanStatus != IScanInstance.SCAN_PROBING) {
						throw new IllegalStateException("Unable to run a probe for a scan that is already running or complete");
					} else {
						if (scanProbe != null) {
							throw new IllegalStateException("Another probe is already in progress");
						}
					}
				} else {
					requestEngine = createRequestEngine(config);
					workspace.getScanAlertRepository().addActiveScanInstance(scanInstance);
				}
				
				scanInstance.updateScanStatus(IScanInstance.SCAN_PROBING);
			}
			
			scanProbe = new ScanProbe(uri, requestEngine);
		}
		final IScanProbeResult probeResult = scanProbe.runProbe();
		synchronized(this) {
			scanProbe = null;
		}
		redirectURI = probeResult.getRedirectTarget();
		return probeResult;
	}

	@Override
	public void startScan() {
		synchronized(this) {
			if (scanInstance == null) {
				throw new IllegalStateException("Scan is detached from workspace; scan instance was lost. A new scan must be created");
			}

			if (config.getScanTargetScope() == null) {
				throw new IllegalArgumentException("Cannot start scan because no target was specified");
			}

			synchronized(scanInstance) {
				final int scanStatus = scanInstance.getScanStatus();
				if (scanStatus != IScanInstance.SCAN_CONFIG) {
					if (scanStatus != IScanInstance.SCAN_PROBING) {
						throw new IllegalStateException("Scan is already running or complete");
					} else {
						if (scanProbe != null) {
							throw new IllegalStateException("A scan probe is in progress");
						}
					}
				} else {
					requestEngine = createRequestEngine(config);
					workspace.getScanAlertRepository().addActiveScanInstance(scanInstance);
				}

				scanInstance.updateScanStatus(IScanInstance.SCAN_STARTING);
			}

			reloadModules();
			scannerTask = new ScannerTask(this);
			scannerThread = new Thread(scannerTask);
			scannerThread.start();
		}
	}

	@Override
	public void stopScan() {
		synchronized(this) {
			if(scannerTask != null) {
				scannerTask.stop();
			} else {
				if(scanProbe != null) {
					scanProbe.abort();
				}

				if (scanInstance != null) {
					synchronized(scanInstance) {
						if (scanInstance.getScanStatus() != IScanInstance.SCAN_CONFIG) {
							scanInstance.updateScanStatus(IScanInstance.SCAN_CANCELLED);
						}
					}
				}

				doFinish();
			}
		}
	}

	private IHttpRequestEngine createRequestEngine(IScannerConfig config) {
		final IHttpRequestEngineFactory requestEngineFactory = scanner.getHttpRequestEngineFactory();
		final IHttpRequestEngineConfig requestEngineConfig = requestEngineFactory.createConfig();
		if (config.getCookieList() != null && !config.getCookieList().isEmpty()) {
			CookieStore cookieStore = requestEngineConfig.getCookieStore();
			for (Cookie c: config.getCookieList()) {
				cookieStore.addCookie(c);
			}
		}		
		if (config.getMaxRequestsPerSecond() > 0) {
			requestEngineConfig.setRequestsPerMinute(config.getMaxRequestsPerSecond() * 60);
		}
		requestEngineConfig.setMaxConnections(config.getMaxConnections());
		requestEngineConfig.setMaxConnectionsPerRoute(config.getMaxConnections());
		requestEngineConfig.setMaximumResponseKilobytes(config.getMaxResponseKilobytes());
		final HttpClient client = requestEngineFactory.createUnencodingClient();
		HttpProtocolParams.setUserAgent(client.getParams(), config.getUserAgent());
		
		final IRequestOriginScanner requestOrigin = workspace.getRequestLog().getRequestOriginScanner(scanInstance);
		IHttpRequestEngine requestEngine = requestEngineFactory.createRequestEngine(client, requestEngineConfig, requestOrigin);
		// REVISIT: consider moving authentication method to request engine config
		IIdentity identity = config.getScanIdentity();
		if (identity != null && identity.getAuthMethod() != null) {
			identity.getAuthMethod().setAuth(requestEngine);
		}
		return requestEngine;
	}

	private void reloadModules() {
		IScannerModuleRegistry moduleRegistry = scanner.getScannerModuleRegistry();
		if(responseProcessingModules == null || basicModules == null) {
			responseProcessingModules = moduleRegistry.getResponseProcessingModules();
			basicModules = moduleRegistry.getBasicModules();
		} else {
			responseProcessingModules = moduleRegistry.updateResponseProcessingModules(responseProcessingModules);
			basicModules = moduleRegistry.updateBasicModules(basicModules);
		}
	}

	public Scanner getScanner() {
		return scanner;
	}

	public synchronized IScanInstance getScanInstance() {
		return scanInstance;
	}

	public synchronized IWorkspace getWorkspace() {
		return workspace;
	}

	public synchronized List<IResponseProcessingModule> getResponseModules() {
		return responseProcessingModules;
	}
	
	public synchronized List<IBasicModuleScript> getBasicModules() {
		return basicModules;
	}

	public synchronized IHttpRequestEngine getRequestEngine() {
		return requestEngine;
	}

	public void doFinish() {
		synchronized(this) {
			scanInstance.setScan(null);
			workspace.getScanAlertRepository().removeActiveScanInstance(scanInstance);
			workspace.unlock();
		}
	}

	public URI getRedirectURI() {
		return redirectURI;
	}
	
}
