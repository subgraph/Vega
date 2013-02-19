package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.HttpContext;

public class VegaResponseProcessCookies implements HttpResponseInterceptor {

	public void process(final HttpResponse response, final HttpContext context)
			throws HttpException, IOException {
		if (response == null) {
			throw new IllegalArgumentException("HTTP request may not be null");
		}
		if (context == null) {
			throw new IllegalArgumentException("HTTP context may not be null");
		}

		// Obtain actual CookieSpec instance
		CookieSpec cookieSpec = (CookieSpec) context
				.getAttribute(ClientContext.COOKIE_SPEC);
		if (cookieSpec == null) {
			return;
		}
		// Obtain cookie store
		CookieStore cookieStore = (CookieStore) context
				.getAttribute(ClientContext.COOKIE_STORE);
		if (cookieStore == null) {
			return;
		}
		// Obtain actual CookieOrigin instance
		CookieOrigin cookieOrigin = (CookieOrigin) context
				.getAttribute(ClientContext.COOKIE_ORIGIN);
		if (cookieOrigin == null) {
			return;
		}
		HeaderIterator it = response.headerIterator(SM.SET_COOKIE);
		processCookies(it, cookieSpec, cookieOrigin, cookieStore);

		// see if the cookie spec supports cookie versioning.
		if (cookieSpec.getVersion() > 0) {
			// process set-cookie2 headers.
			// Cookie2 will replace equivalent Cookie instances
			it = response.headerIterator(SM.SET_COOKIE2);
			processCookies(it, cookieSpec, cookieOrigin, cookieStore);
		}
	}

	private void processCookies(final HeaderIterator iterator,
			final CookieSpec cookieSpec, final CookieOrigin cookieOrigin,
			final CookieStore cookieStore) {
		while (iterator.hasNext()) {
			Header header = iterator.nextHeader();
			try {
				List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
				for (Cookie cookie : cookies) {
					cookieStore.addCookie(cookie);
				}
			} catch (MalformedCookieException ex) {
			}

		}
	}

}
