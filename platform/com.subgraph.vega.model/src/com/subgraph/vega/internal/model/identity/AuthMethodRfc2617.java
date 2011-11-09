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
import org.apache.http.auth.UsernamePasswordCredentials;
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
import com.subgraph.vega.api.model.identity.IAuthMethodRfc2617;

public class AuthMethodRfc2617 extends AbstractAuthMethod implements IAuthMethodRfc2617 {
	private AuthScheme authScheme;
	private String username;
	private String password;
	private String charset;
	
	protected AuthMethodRfc2617() {
		super(IAuthMethod.AuthMethodType.AUTH_METHOD_RFC2617);
		authScheme = IAuthMethodRfc2617.AuthScheme.AUTH_SCHEME_DIGEST;
	}

	@Override
	public void setAuthScheme(AuthScheme authScheme) {
		activate(ActivationPurpose.READ);
		this.authScheme = authScheme;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public AuthScheme getAuthScheme() {
		activate(ActivationPurpose.READ);
		return authScheme;
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
	public void setCharset(String charset) {
		activate(ActivationPurpose.READ);
		this.charset = charset;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getCharset() {
		activate(ActivationPurpose.READ);
		return charset;	
	}

	@Override
	public void setAuth(IHttpRequestEngine requestEngine) {
		activate(ActivationPurpose.READ);

		HttpClient httpClient = requestEngine.getHttpClient();
		List<String> authPref = new ArrayList<String>(1);
		switch (authScheme) {
		case AUTH_SCHEME_BASIC:
			authPref.add(AuthPolicy.BASIC);
			break;
		case AUTH_SCHEME_DIGEST:
			authPref.add(AuthPolicy.DIGEST);
			break;
		}
		httpClient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authPref);

		HttpContext httpContext = requestEngine.getHttpContext();
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, creds);
		httpContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
	}

}
