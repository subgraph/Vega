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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.subgraph.vega.api.scanner.IFormCredential;
import com.subgraph.vega.api.util.VegaURI;

public class FormProcessingState {
	private final static Logger logger = Logger.getLogger("scanner");
	private final static FormHints formHints = new FormHints();

	//private final List<IFormCredential> credentials;
	private final VegaURI baseURI;
	private final String action;
	private final String method;

	private final List<NameValuePair> parameters = new ArrayList<NameValuePair>();

	private VegaURI cachedTargetURI;
	private boolean passwordFlag;
	private boolean fileFieldFlag;

	FormProcessingState(VegaURI baseURI, String action, String method, List<IFormCredential> credentials) {
		this.baseURI = baseURI;
		this.action = action;
		this.method = method;
		//this.credentials = credentials;
	}

	boolean isValid() {
		return (getTargetURI() != null);
	}

	boolean isPostMethod() {
		return (method != null && method.toLowerCase().equals("post"));
	}

	VegaURI getTargetURI() {
		synchronized(this) {
			if(cachedTargetURI == null) {
				cachedTargetURI = createTargetURI();
			}
			return cachedTargetURI;
		}
	}

	private VegaURI createTargetURI() {
		if(baseURI == null)
			return null;
		if(action == null || action.trim().equals("#")) {
			return baseURI;
		}
		try {
			final VegaURI target = baseURI.resolve(action);
			final String scheme = target.getTargetHost().getSchemeName();
			if(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
				return target;
			else
				return null;
		} catch (IllegalArgumentException e) {
			logger.log(Level.WARNING, "Failed to create new URI from base: "+ baseURI + " and action="+ action, e);
			return null;
		}
	}

	void add(String name, String value) {
		parameters.add(new BasicNameValuePair(name, (value == null) ? ("") : (value)));
	}

	void addGuessedValue(String name) {
		add(name, guessFormValue(name));
	}

	void setPasswordFieldFlag() {
		passwordFlag = true;
	}

	boolean getPasswordFieldFlag() {
		return passwordFlag;
	}

	void setFileFieldFlag() {
		fileFieldFlag = true;
	}

	boolean getFileFieldFlag() {
		return fileFieldFlag;
	}

	/*
	private boolean isPossiblePasswordField(String name) {
		final String n = name.toLowerCase();
		return (n.contains("pass") || n.contains("pwd"));
	}

	private boolean isPossibleLoginField(String name) {
		final String n = name.toLowerCase();
		return (n.contains("name") || n.contains("user") || n.contains("log"));
	}
	*/
	private String guessFormValue(String name) {
		/*
		if(config.getNtlmPassword() != null && isPossiblePasswordField(name))
			return config.getNtlmPassword();
		if(config.getNtlmUsername() != null && isPossibleLoginField(name))
			return config.getNtlmUsername();
			*/
		return formHints.lookupHint(name);
	}

	List<NameValuePair> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		if(isPostMethod()) {
			return "POST "+ getTargetURI().toString() + parametersAsPostString();
		} else {
			return "GET " + getTargetURI().toString() + parametersAsQueryString();
		}
	}

	private String parametersAsQueryString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("?");
		for(NameValuePair nvp: parameters) {
			if(sb.length() > 1)
				sb.append("&");
			sb.append(nvp.getName());
			if(nvp.getValue() != null) {
				sb.append("=");
				sb.append(nvp.getValue());
			}
		}
		return sb.toString();
	}

	private String parametersAsPostString() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append(" [");
		for(NameValuePair nvp: parameters) {
			if(first)
				first = false;
			else
				sb.append(", ");
			sb.append(nvp.getName());
			if(nvp.getValue() != null) {
				sb.append("=");
				sb.append(nvp.getValue());
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
