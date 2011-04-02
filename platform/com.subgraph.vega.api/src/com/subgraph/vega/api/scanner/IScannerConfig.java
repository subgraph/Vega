package com.subgraph.vega.api.scanner;

import java.net.URI;
import java.util.List;

public interface IScannerConfig {
	
	void setBaseURI(URI baseURI);
	void setCookieString(String cookieString);
	void setBasicUsername(String username);
	void setBasicPassword(String password);
	void setBasicRealm(String realm);
	void setBasicDomain(String domain);
	void setNtlmUsername(String username);
	void setNtlmPassword(String password);
	void setModulesList(List<String> modules);
	void setExclusions(List<String> exclusions);
	void setLogAllRequests(boolean flag);
	String getCookieString();
	String getBasicUsername();
	String getBasicPassword();
	String getBasicRealm();
	String getBasicDomain();
	String getNtlmUsername();
	String getNtlmPassword();
	URI getBaseURI();
	List<String> getModulesList();
	List<String> getExclusions();
	boolean getLogAllRequests();
}
