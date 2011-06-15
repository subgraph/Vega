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
package com.subgraph.vega.impl.scanner.forms;

import com.subgraph.vega.api.scanner.IFormCredential;

public class FormCredential implements IFormCredential {

	private final String username;
	private final String password;

	private String usernameField;
	private String passwordField;
	private String targetName;

	public FormCredential(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void setTargetName(String name) {
		this.targetName = name;
	}

	@Override
	public void setUsernameFieldName(String name) {
		this.usernameField = name;
	}

	@Override
	public void setPasswordFieldName(String name) {
		this.passwordField = name;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getTargetName() {
		return targetName;
	}

	@Override
	public String getUsernameFieldName() {
		return usernameField;
	}

	@Override
	public String getPasswordFieldName() {
		return passwordField;
	}
}
