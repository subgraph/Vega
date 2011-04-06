package com.subgraph.vega.api.scanner;


public interface IFormCredential {
	void setTargetName(String name);
	void setUsernameFieldName(String name);
	void setPasswordFieldName(String name);

	String getUsername();
	String getPassword();

	String getTargetName();
	String getUsernameFieldName();
	String getPasswordFieldName();
}
