package com.subgraph.vega.internal.model.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.http.requests.IHttpRequestModifier;
import com.subgraph.vega.api.model.identity.IAuthMethodRfc2617.AuthScheme;

public class RequestModifierRfc2617 implements IHttpRequestModifier {
	private final AuthScheme authScheme;
	private final String username;
	private final String password;

	public RequestModifierRfc2617(AuthScheme authScheme, String username, String password) {
		this.authScheme = authScheme;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void process(HttpRequest request, HttpContext context) {
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY, creds);
		context.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);
	}

	public void bind(IHttpRequestEngine requestEngine) {
		final HttpClient httpClient = requestEngine.getHttpClient();
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
		requestEngine.addRequestModifier(this);
	}
	
}
