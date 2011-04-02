package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.List;

import com.subgraph.vega.api.scanner.IScannerConfig;

public class ScannerConfig implements IScannerConfig {
	private URI baseURI;
	private String cookieString;
	private List<String> modulesList;
	private List<String> exclusions;
	private String basicUsername;
	private String basicPassword;
	private String basicRealm;
	private String basicDomain;
	private String ntlmUsername;
	private String ntlmPassword;
	private boolean logAllRequests;
	
	@Override
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;		
	}
	
	@Override
	public void setCookieString(String cookieString) {
		this.cookieString = cookieString;
	}
	
	@Override
	public void setModulesList(List<String> modules) {
		modulesList = modules;
	}
	
	@Override 
	public void setExclusions(List<String> exclusionsList) {
		exclusions = exclusionsList;
	}
	
	@Override
	public void setBasicUsername(String username) {
		basicUsername = username;
	}
	
	@Override
	public void setBasicPassword(String password) {
		basicPassword = password;
	}
	
	@Override
	public void setBasicRealm(String realm) {
		basicRealm = realm;
	}
	
	@Override
	public void setBasicDomain(String domain) {
		basicDomain = domain;
	}

	@Override
	public void setNtlmUsername(String username) {
		ntlmUsername = username;
	}
	
	@Override
	public void setNtlmPassword(String password) {
		ntlmPassword = password;
	}
	
	@Override
 	public URI getBaseURI() {
		return baseURI;
	}
	
	@Override
	public String getCookieString() {
		return cookieString;
	}

	@Override
	public List<String> getModulesList() {
		return modulesList;
	}
	
	@Override
	public List<String> getExclusions() {
		return exclusions;
	}
	
	@Override
	public String getBasicUsername() {
		return basicUsername;
	}
	
	@Override
	public String getBasicPassword() {
		return basicPassword;
	}
	
	@Override
	public String getBasicRealm() {
		return basicRealm;
	}
	
	@Override
	public String getBasicDomain() {
		return basicDomain;
	}
	
	@Override
	public String getNtlmUsername() {
		return ntlmUsername;
	}
	
	@Override
	public String getNtlmPassword() {
		return ntlmPassword;
	}

	@Override
	public void setLogAllRequests(boolean flag) {
		logAllRequests = flag;
	}

	@Override
	public boolean getLogAllRequests() {
		return logAllRequests;
	}
	
}

