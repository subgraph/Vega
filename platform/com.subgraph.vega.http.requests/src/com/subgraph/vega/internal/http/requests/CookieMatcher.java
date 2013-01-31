package com.subgraph.vega.internal.http.requests;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.impl.client.AbstractHttpClient;

public class CookieMatcher {
	private final AbstractHttpClient client;
	
	CookieMatcher(AbstractHttpClient client) {
		this.client = client;
	}
			
	public List<Cookie> getCookiesForRequest(HttpHost targetHost,
			HttpRequest request) {
		CookieSpecRegistry registry = client.getCookieSpecs();
		final String policy = HttpClientParams.getCookiePolicy(request.getParams());
		final CookieOrigin origin = getCookieOrigin(targetHost, request);
		final CookieSpec cookieSpec = registry.getCookieSpec(policy, request.getParams());
		final List<Cookie> matchedCookies = new ArrayList<Cookie>();
		final Date now = new Date();
		for(Cookie cookie: client.getCookieStore().getCookies()) {
			if(cookieSpec.match(cookie, origin) && !cookie.isExpired(now)) {
				matchedCookies.add(cookie);
			}
		}
		return Collections.unmodifiableList(matchedCookies);
	}
	
	private CookieOrigin getCookieOrigin(HttpHost host, HttpRequest request) {
		final int port = getPortForHost(host);
		final String path = getPathForRequest(request);
		return new CookieOrigin(host.getHostName(), port, path, isSecureScheme(host));
	}
	
	private int getPortForHost(HttpHost host) {
		if(host.getPort() > 0) {
			return host.getPort();
		}
		final String scheme = host.getSchemeName();
		if("http".equalsIgnoreCase(scheme)) {
			return 80;
		} else if("https".equalsIgnoreCase(scheme)) {
			return 443;
		} else {
			return 0;
		}
	}
	
	private boolean isSecureScheme(HttpHost host) {
		return "https".equalsIgnoreCase(host.getSchemeName());
	}
	
	private String getPathForRequest(HttpRequest request) {
		if(request instanceof HttpUriRequest) {
			final URI requestURI = ((HttpUriRequest) request).getURI();
			return requestURI.getPath();
		} else {
			return request.getRequestLine().getUri();
		}
	}
}
