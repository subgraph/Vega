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

import java.util.List;
import java.util.Set;

import org.apache.http.cookie.Cookie;

import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.scope.ITargetScope;

public interface IScannerConfig {
	final static int DEFAULT_MAX_DESCENDANTS = 8192;
	final static int DEFAULT_MAX_CHILDREN = 512;
	final static int DEFAULT_MAX_DEPTH = 16;
	final static int DEFAULT_MAX_DUPLICATE_PATHS = 3;
	final static int DEFAULT_MAX_PARAMETER_COUNT = 16;
	final static int DEFAULT_MAX_REQUEST_PER_SECOND = 25;
	final static int DEFAULT_MAX_CONNECTIONS = 50;
	final static int DEFAULT_MAX_RESPONSE_KILOBYTES = 1024;

	void setScanTargetScope(ITargetScope scope);

	/**
	 * Set the website identity the scan will be performed as. 
	 * @param scanIdentity Scan identity to perform the scan as, or null.
	 */
	void setScanIdentity(IIdentity scanIdentity);
	
	void setUserAgent(String userAgent);
	void setCookieList(List<Cookie> list);
//	void setBasicUsername(String username);
//	void setBasicPassword(String password);
//	void setBasicRealm(String realm);
//	void setBasicDomain(String domain);
//	void setNtlmUsername(String username);
//	void setNtlmPassword(String password);
	void setModulesList(List<String> modules);

	void setExcludedParameterNames(Set<String> names);
	void setLogAllRequests(boolean flag);
	void setDisplayDebugOutput(boolean flag);
	void setMaxRequestsPerSecond(int rps);
	void setMaxDescendants(int value);
	void setMaxChildren(int value);
	void setMaxDepth(int value);
	void setMaxDuplicatePaths(int value);
	void setMaxParameterCount(int value);
	void setMaxConnections(int value);
	void setMaxResponseKilobytes(int kb);
	
	List<Cookie> getCookieList();
//	String getBasicUsername();
//	String getBasicPassword();
//	String getBasicRealm();
//	String getBasicDomain();
//	String getNtlmUsername();
//	String getNtlmPassword();
	ITargetScope getScanTargetScope();
	String getUserAgent();

	/**
	 * Get the identity the scan will be performed as. 
	 * @return Scan identity to perform the scan as, or null if none is set.
	 */
	IIdentity getScanIdentity();
	
	List<String> getModulesList();
	
	Set<String> getDefaultExcludedParameterNames();
	Set<String> getExcludedParameterNames();

	IFormCredential createFormCredential(String username, String password) ; // XXX
	List<IFormCredential> getFormCredentials(); // XXX
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
	int getMaxResponseKilobytes();
}
