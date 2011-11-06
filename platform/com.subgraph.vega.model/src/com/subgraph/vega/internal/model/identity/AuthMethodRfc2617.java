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
		final RequestModifierRfc2617 requestModifier = new RequestModifierRfc2617(authScheme, username, password);
		requestModifier.bind(requestEngine);
	}

}
