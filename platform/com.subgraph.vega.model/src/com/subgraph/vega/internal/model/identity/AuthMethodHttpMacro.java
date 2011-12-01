package com.subgraph.vega.internal.model.identity;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacro;

public class AuthMethodHttpMacro extends AbstractAuthMethod implements IAuthMethodHttpMacro {
	private IHttpMacro macro;

	protected AuthMethodHttpMacro() {
		super(IAuthMethod.AuthMethodType.AUTH_METHOD_HTTP_MACRO);
	}

	@Override
	public void setMacro(IHttpMacro macro) {
		activate(ActivationPurpose.READ);
		this.macro = macro;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public IHttpMacro getMacro() {
		activate(ActivationPurpose.READ);
		return macro;
	}

	@Override
	public void setAuth(IHttpRequestEngine requestEngine) {
		// authentication is handled externally for now
	}

}
