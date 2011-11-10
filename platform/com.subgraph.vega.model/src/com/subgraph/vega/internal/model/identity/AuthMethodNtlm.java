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
package com.subgraph.vega.internal.model.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodNtlm;

public class AuthMethodNtlm extends AbstractAuthMethod implements IAuthMethodNtlm {
	private String username;
	private String password;
	private String workstation;
	private String domain;
	
	protected AuthMethodNtlm() {
		super(IAuthMethod.AuthMethodType.AUTH_METHOD_NTLM);
	}

	@Override
	public void setUsername(String username) {
		activate(ActivationPurpose.READ);
		this.username = username;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getUsername() {
		activate(ActivationPurpose.READ);
		return username;
	}

	@Override
	public void setPassword(String password) {
		activate(ActivationPurpose.READ);
		this.password = password;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getPassword() {
		activate(ActivationPurpose.READ);
		return password;
	}

	@Override
	public void setWorkstation(String workstation) {
		activate(ActivationPurpose.READ);
		this.workstation = workstation;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getWorkstation() {
		activate(ActivationPurpose.READ);
		return workstation;
	}

	@Override
	public void setDomain(String domain) {
		activate(ActivationPurpose.READ);
		this.domain = domain;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getDomain() {
		activate(ActivationPurpose.READ);
		return domain;
	}

	@Override
	public void setAuth(IHttpRequestEngine requestEngine) {
		activate(ActivationPurpose.READ);

		HttpClient httpClient = requestEngine.getHttpClient();
		List<String> authPref = new ArrayList<String>(1);
		authPref.add(AuthPolicy.NTLM);
		httpClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authPref);

		HttpContext httpContext = requestEngine.getHttpContext();
		NTCredentials creds = new NTCredentials(username, password, workstation, domain);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, creds);
		httpContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
	}

}
