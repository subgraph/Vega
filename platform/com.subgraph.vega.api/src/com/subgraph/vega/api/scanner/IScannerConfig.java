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
package com.subgraph.vega.api.scanner;

import java.net.URI;
import java.util.List;

import org.apache.http.cookie.Cookie;

public interface IScannerConfig {
	final static int DEFAULT_MAX_DESCENDANTS = 8192;
	final static int DEFAULT_MAX_CHILDREN = 512;
	final static int DEFAULT_MAX_DEPTH = 16;
	final static int DEFAULT_MAX_DUPLICATE_PATHS = 3;
	final static int DEFAULT_MAX_PARAMETER_COUNT = 16;
	final static int DEFAULT_MAX_REQUEST_PER_SECOND = 25;
	final static int DEFAULT_MAX_CONNECTIONS = 16;

	void setBaseURI(URI baseURI);
	void setCookieList(List<Cookie> list);
	void setBasicUsername(String username);
	void setBasicPassword(String password);
	void setBasicRealm(String realm);
	void setBasicDomain(String domain);
	void setNtlmUsername(String username);
	void setNtlmPassword(String password);
	void setModulesList(List<String> modules);
	void setExclusions(List<String> exclusions);
	void setLogAllRequests(boolean flag);
	void setDisplayDebugOutput(boolean flag);
	void setMaxRequestsPerSecond(int rps);
	void setMaxDescendants(int value);
	void setMaxChildren(int value);
	void setMaxDepth(int value);
	void setMaxDuplicatePaths(int value);
	void setMaxParameterCount(int value);
	void setMaxConnections(int value);
	
	List<Cookie> getCookieList();
	String getBasicUsername();
	String getBasicPassword();
	String getBasicRealm();
	String getBasicDomain();
	String getNtlmUsername();
	String getNtlmPassword();
	URI getBaseURI();
	List<String> getModulesList();
	List<String> getExclusions();
	IFormCredential createFormCredential(String username, String password) ;
	List<IFormCredential> getFormCredentials();
	boolean getLogAllRequests();
	boolean getDisplayDebugOutput();
	boolean getDirectoryInjectionChecksFlag();
	boolean getNonParameterFileInjectionChecksFlag();
	int getMaxRequestsPerSecond();
	int getMaxDescendants();
	int getMaxChildren();
	int getMaxDepth();
	int getMaxDuplicatePaths();
	int getMaxParameterCount();
	int getMaxConnections();
}
