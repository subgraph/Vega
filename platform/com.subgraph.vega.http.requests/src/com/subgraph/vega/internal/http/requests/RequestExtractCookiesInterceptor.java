package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
 
 public class RequestExtractCookiesInterceptor implements HttpRequestInterceptor {
 

	 /* Copied from BrowserCompatSpec */
	 private static final List<String> DATE_PATTERNS = Arrays.asList(
        DateUtils.PATTERN_RFC1123,
        DateUtils.PATTERN_RFC1036,
        DateUtils.PATTERN_ASCTIME,
        "EEE, dd-MMM-yyyy HH:mm:ss z",
        "EEE, dd-MMM-yyyy HH-mm-ss z",
        "EEE, dd MMM yy HH:mm:ss z",
        "EEE dd-MMM-yyyy HH:mm:ss z",
        "EEE dd MMM yyyy HH:mm:ss z",
        "EEE dd-MMM-yyyy HH-mm-ss z",
        "EEE dd-MMM-yy HH:mm:ss z",
        "EEE dd MMM yy HH:mm:ss z",
        "EEE,dd-MMM-yy HH:mm:ss z",
        "EEE,dd-MMM-yyyy HH:mm:ss z",
        "EEE, dd-MM-yyyy HH:mm:ss z",
        
        // Added this pattern seen in the wild
        "EEE, dd MMM yyyy HH:mm:ss"
	 );

	 private final String defaultPolicy;
	 
	 public RequestExtractCookiesInterceptor(String defaultPolicy) {
		 this.defaultPolicy = defaultPolicy;
	 }
	
	 public RequestExtractCookiesInterceptor() {
		 this(CookiePolicy.BROWSER_COMPATIBILITY);
	 }

	 @Override
	 public void process(HttpRequest request, HttpContext context)
			 throws HttpException, IOException {
 
		 final CookieOrigin trueOrigin = getTrueOrigin(request, context);
		 if(trueOrigin == null) {
			 return;
		 }
		 final CookieOrigin cookieOrigin = toCookieOrigin(trueOrigin);
		 
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
		 context.setAttribute(ClientContext.COOKIE_ORIGIN, trueOrigin);
	 }
	 
	 private void addCookiesForHeader(Header header, CookieSpec spec, CookieOrigin origin, CookieStore store) throws MalformedCookieException {
		 for(Cookie c: processCookieHeader(header, spec, origin)) {
			 store.addCookie(c);
		 }
	 }
	
	 private List<Cookie> processCookieHeader(Header header, CookieSpec spec, CookieOrigin origin) {
		 final List<Cookie> cookies = new ArrayList<Cookie>();
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
		 
		 final HttpParams params = new DefaultedHttpParams(new BasicHttpParams(), request.getParams());
		 params.setParameter(CookieSpecPNames.DATE_PATTERNS, DATE_PATTERNS);
 		 final String policy = getCookiePolicy(params);
 		 return registry.getCookieSpec(policy, params);
	 }
	 
	 
	 private String getCookiePolicy(HttpParams params) {
		 final String policy = (String) params.getParameter(ClientPNames.COOKIE_POLICY);
		 if(policy == null) {
			 return defaultPolicy;
		 } else {
			 return policy;
		 }
	 }
 
	 private CookieOrigin getTrueOrigin(HttpRequest request, HttpContext context) throws ProtocolException {
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
				 getPathForRequest(request),
				 conn.isSecure());
	 }

	 private CookieOrigin toCookieOrigin(CookieOrigin trueOrigin) {
		 return new CookieOrigin(
				 trueOrigin.getHost(), 
				 trueOrigin.getPort(), 
				 "/", // broaden path scope since original cookie path is unknown 
				 false);
	 }

	 private String getPathForRequest(HttpRequest request) {
		if(request instanceof HttpUriRequest) {
			final URI requestURI = ((HttpUriRequest) request).getURI();
			return requestURI.getPath();
		} else {
			return request.getRequestLine().getUri();
		}
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
