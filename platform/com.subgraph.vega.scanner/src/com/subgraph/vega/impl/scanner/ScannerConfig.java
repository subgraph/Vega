package com.subgraph.vega.impl.scanner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;

import com.subgraph.vega.api.scanner.IFormCredential;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.impl.scanner.forms.FormCredential;

public class ScannerConfig implements IScannerConfig {
	
	private URI baseURI;
	private List<Cookie> cookieList;
	private List<String> modulesList;
	private List<String> exclusions;
	private String basicUsername;
	private String basicPassword;
	private String basicRealm;
	private String basicDomain;
	private String ntlmUsername;
	private String ntlmPassword;
	private boolean logAllRequests;
	private boolean displayDebugOutput;
	private int maxRequestsPerSecond = DEFAULT_MAX_REQUEST_PER_SECOND;
	private int maxDescendants = DEFAULT_MAX_DESCENDANTS;
	private int maxChildren = DEFAULT_MAX_CHILDREN;
	private int maxDepth = DEFAULT_MAX_DEPTH;
	private int maxDuplicatePaths = DEFAULT_MAX_DUPLICATE_PATHS;
	private int maxParameterCount = DEFAULT_MAX_PARAMETER_COUNT;
	
	private final List<IFormCredential> formCredentials = new ArrayList<IFormCredential>();

	@Override
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
	}

	@Override
	public void setCookieList(List<Cookie> list) {
		cookieList = list;
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
	public List<Cookie> getCookieList() {
		return cookieList;
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

	@Override
	public void setDisplayDebugOutput(boolean flag) {
		displayDebugOutput = flag;
	}

	@Override
	public boolean getDisplayDebugOutput() {
		return displayDebugOutput;
	}

	@Override
	public boolean getDirectoryInjectionChecksFlag() {
		return true;
	}

	@Override
	public boolean getNonParameterFileInjectionChecksFlag() {
		return false;
	}

	public IFormCredential createFormCredential(String username, String password) {
		final IFormCredential credential = new FormCredential(username, password);
		formCredentials.add(credential);
		return credential;
	}

	@Override
	public List<IFormCredential> getFormCredentials() {
		return formCredentials;
	}

	@Override
	public void setMaxRequestsPerSecond(int rps) {
		maxRequestsPerSecond = rps;
	}

	@Override
	public int getMaxRequestsPerSecond() {
		return maxRequestsPerSecond;
	}

	@Override
	public int getMaxDescendants() {
		return maxDescendants;
	}

	@Override
	public int getMaxChildren() {
		return maxChildren;
	}

	@Override
	public int getMaxDepth() {
		return maxDepth;
	}

	@Override
	public void setMaxDescendants(int value) {
		maxDescendants = value;
	}

	@Override
	public void setMaxChildren(int value) {
		maxChildren = value;
	}

	@Override
	public void setMaxDepth(int value) {
		maxDepth = value;
	}

	@Override
	public int getMaxDuplicatePaths() {
		return maxDuplicatePaths;
	}

	@Override
	public void setMaxDuplicatePaths(int value) {
		maxDuplicatePaths = value;
	}

	@Override
	public void setMaxParameterCount(int value) {
		this.maxParameterCount = value;
	}

	@Override
	public int getMaxParameterCount() {
		return maxParameterCount;
	}
}

