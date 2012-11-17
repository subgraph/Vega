package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
 
 public class RequestExtractCookiesInterceptor implements HttpRequestInterceptor {
 
	 @Override
	 public void process(HttpRequest request, HttpContext context)
			 throws HttpException, IOException {
 
		 final CookieOrigin cookieOrigin = getCookieOrigin(request, context);
		 if(cookieOrigin == null) {
			 return;
		 }
		 
		 final CookieStore cookieStore = (CookieStore) context.getAttribute(ClientContext.COOKIE_STORE);
		 if(cookieStore == null) {
			 return;
		 }
		 
		 final CookieSpec cookieSpec = getCookieSpec(request, context);
		 if(cookieSpec == null) {
			 return;
		 }
		 
		 for(Header h: request.getHeaders(SM.COOKIE)) {
			 addCookiesForHeader(h, cookieSpec, cookieOrigin, cookieStore);
		 }
		 
		 context.setAttribute(ClientContext.COOKIE_SPEC, cookieSpec);
		 context.setAttribute(ClientContext.COOKIE_ORIGIN, cookieOrigin);
	 }
	 
	 private void addCookiesForHeader(Header header, CookieSpec spec, CookieOrigin origin, CookieStore store) throws MalformedCookieException {
		 for(Cookie c: processCookieHeader(header, spec, origin)) {
			 store.addCookie(c);
		 }
	 }
	
	 private List<Cookie> processCookieHeader(Header header, CookieSpec spec, CookieOrigin origin) {
		 final List<Cookie> cookies = new ArrayList<>();
		 for(String cookieValue: header.getValue().split(";")) {
			 try {
				 final Header setHeader = new BasicHeader(SM.SET_COOKIE, cookieValue);
				 final List<Cookie> parsedCookies = spec.parse(setHeader, origin);
				 cookies.addAll(parsedCookies);
			} catch (MalformedCookieException e) {
				// Just ignore it
			}
		 }
		 return cookies;
	 }
	 
	 private CookieSpec getCookieSpec(HttpRequest request, HttpContext context) {
		 final CookieSpecRegistry registry = (CookieSpecRegistry) context.getAttribute(
				 ClientContext.COOKIESPEC_REGISTRY);
		 if(registry == null) {
			 return null;
		 }
 		 final String policy = HttpClientParams.getCookiePolicy(request.getParams());
 		 return registry.getCookieSpec(policy, request.getParams());
	 }
	 
	 private CookieOrigin getCookieOrigin(HttpRequest request, HttpContext context) throws ProtocolException {
		 final HttpHost targetHost = (HttpHost) context.getAttribute(
				 ExecutionContext.HTTP_TARGET_HOST);
		 if(targetHost == null) {
			 return null;
		 }
		 final HttpRoutedConnection conn = (HttpRoutedConnection) context.getAttribute(
				 ExecutionContext.HTTP_CONNECTION);
		 if(conn == null) {
			 return null;
		 }
		 
		 return new CookieOrigin(
				 targetHost.getHostName(),
				 getPort(targetHost, conn),
				 "/", // broaden path scope since original cookie path is unknown
				 false);
	 }

	 private int getPort(HttpHost host, HttpRoutedConnection connection) {
		 if(host.getPort() >= 0) {
			 return host.getPort();
		 } else if (connection.getRoute().getHopCount() == 1) {
			 return connection.getRemotePort();
		 } else {
			 return getPortForScheme(host.getSchemeName());
		 }
	}

	private int getPortForScheme(String scheme) {
		if(scheme.equalsIgnoreCase("http")) {
			return 80;
		} else if(scheme.equalsIgnoreCase("https")) {
			return 443;
		} else {
			return 0;
		}
	}
}
