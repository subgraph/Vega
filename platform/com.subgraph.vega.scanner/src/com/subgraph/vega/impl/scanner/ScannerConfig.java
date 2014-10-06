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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.cookie.Cookie;

import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IFormCredential;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.impl.scanner.forms.FormCredential;

public class ScannerConfig implements IScannerConfig {

	private final static String[] defaultExcludedParameters = { "xsrftoken", "__eventvalidation", "__eventargument", "__eventtarget", "__viewstateencrypted", "__viewstate", "anticsrf", "csrfmiddlewaretoken", "csrftoken" };
	private ITargetScope scanTargetScope;
	private String userAgent = IHttpRequestEngineFactory.DEFAULT_USER_AGENT;
	private IIdentity scanIdentity;
	private List<Cookie> cookieList;
	private List<String> modulesList;
	private final Set<String> excludedParameterNames = new HashSet<String>();
	private boolean logAllRequests;
	private boolean displayDebugOutput;
	private int maxRequestsPerSecond = DEFAULT_MAX_REQUEST_PER_SECOND;
	private int maxDescendants = DEFAULT_MAX_DESCENDANTS;
	private int maxChildren = DEFAULT_MAX_CHILDREN;
	private int maxDepth = DEFAULT_MAX_DEPTH;
	private int maxDuplicatePaths = DEFAULT_MAX_DUPLICATE_PATHS;
	private int maxParameterCount = DEFAULT_MAX_PARAMETER_COUNT;
	private int maxConnections = DEFAULT_MAX_CONNECTIONS;
	private int maxResponseKilobytes = DEFAULT_MAX_RESPONSE_KILOBYTES;
	private final List<IFormCredential> formCredentials = new ArrayList<IFormCredential>();

	@Override
	public synchronized void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public synchronized void setScanIdentity(IIdentity scanIdentity) {
		this.scanIdentity = scanIdentity;
	}

	@Override
	public synchronized void setCookieList(List<Cookie> list) {
		cookieList = list;
	}
	
	@Override
	public synchronized void setModulesList(List<String> modules) {
		modulesList = modules;
	}

	@Override
	public synchronized void setExcludedParameterNames(Set<String> names) {
		excludedParameterNames.clear();
		excludedParameterNames.addAll(names);
	}

	@Override
	public Set<String> getDefaultExcludedParameterNames() {
		return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(defaultExcludedParameters)));
	}

	@Override
	public synchronized Set<String> getExcludedParameterNames() {
		return Collections.unmodifiableSet(new HashSet<String>(excludedParameterNames));
	}

	@Override
	public synchronized String getUserAgent() {
		return userAgent;
	}

	@Override
	public synchronized IIdentity getScanIdentity() {
		return scanIdentity;
	}

	@Override
	public synchronized List<Cookie> getCookieList() {
		return cookieList;
	}

	@Override
	public synchronized List<String> getModulesList() {
		return modulesList;
	}

	@Override
	public synchronized void setLogAllRequests(boolean flag) {
		logAllRequests = flag;
	}

	@Override
	public synchronized boolean getLogAllRequests() {
		return logAllRequests;
	}

	@Override
	public synchronized void setDisplayDebugOutput(boolean flag) {
		displayDebugOutput = flag;
	}

	@Override
	public synchronized boolean getDisplayDebugOutput() {
		return displayDebugOutput;
	}

	@Override
	public synchronized boolean getDirectoryInjectionChecksFlag() {
		return true;
	}

	@Override
	public synchronized boolean getNonParameterFileInjectionChecksFlag() {
		return true;
	}

	public synchronized IFormCredential createFormCredential(String username, String password) {
		final IFormCredential credential = new FormCredential(username, password);
		formCredentials.add(credential);
		return credential;
	}

	@Override
	public synchronized List<IFormCredential> getFormCredentials() {
		return formCredentials;
	}

	@Override
	public synchronized void setMaxRequestsPerSecond(int rps) {
		maxRequestsPerSecond = rps;
	}

	@Override
	public synchronized int getMaxRequestsPerSecond() {
		return maxRequestsPerSecond;
	}

	@Override
	public synchronized int getMaxDescendants() {
		return maxDescendants;
	}

	@Override
	public synchronized int getMaxChildren() {
		return maxChildren;
	}

	@Override
	public synchronized int getMaxDepth() {
		return maxDepth;
	}

	@Override
	public synchronized void setMaxDescendants(int value) {
		maxDescendants = value;
	}

	@Override
	public synchronized void setMaxChildren(int value) {
		maxChildren = value;
	}

	@Override
	public synchronized void setMaxDepth(int value) {
		maxDepth = value;
	}

	@Override
	public synchronized int getMaxDuplicatePaths() {
		return maxDuplicatePaths;
	}

	@Override
	public synchronized void setMaxDuplicatePaths(int value) {
		maxDuplicatePaths = value;
	}

	@Override
	public synchronized void setMaxParameterCount(int value) {
		this.maxParameterCount = value;
	}

	@Override
	public synchronized int getMaxParameterCount() {
		return maxParameterCount;
	}

	@Override
	public synchronized void setMaxConnections(int value) {
		maxConnections = value;
	}

	@Override
	public synchronized int getMaxConnections() {
		return maxConnections;
	}

	@Override
	public synchronized void setMaxResponseKilobytes(int kb) {
		maxResponseKilobytes = kb;
	}

	@Override
	public synchronized int getMaxResponseKilobytes() {
		return maxResponseKilobytes;
	}

	@Override
	public void setScanTargetScope(ITargetScope scope) {
		scanTargetScope = scope;
	}

	@Override
	public ITargetScope getScanTargetScope() {
		return scanTargetScope;
	}
}

