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
package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.List;

import org.apache.http.Header;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IHttpResponseCookie;

public class ResponseJS extends ScriptableObject {

	private static final long serialVersionUID = -1;
	private IHttpResponse response;
	private Scriptable cachedDocument;
	
	public ResponseJS() {
		response = null;
		cachedDocument = null;
	}
	
	public ResponseJS(Object response) {
		this.response = (IHttpResponse) Context.jsToJava(response, IHttpResponse.class);
	}
	
	
	private Scriptable createCachedDocument() {
		final IHTMLParseResult htmlResult = response.getParsedHTML();
		if(htmlResult == null) {
			return null;
		}
		final Context cx = Context.getCurrentContext();
		final HTMLDocument domDocument = htmlResult.getDOMDocument();
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		final Object docOb = Context.javaToJS(domDocument, scope);
		final Object[] args = { docOb };
		return cx.newObject(scope, "HTMLDocument", args);
	}

	IHttpResponse getResponse() {
		return response;
	}

	public int jsGet_code() {
		return response.getResponseCode();
	}

	public boolean jsFunction_hasHeader(String name) {
		return response.getRawResponse().containsHeader(name);
	}

	public Object jsFunction_getFirstHeader(String name) {
		final Header hdr = response.getRawResponse().getFirstHeader(name);
		if(hdr == null) {
			return null;
		}
		final Scriptable parent = ScriptableObject.getTopLevelScope(this);
		return Context.javaToJS(hdr, parent);
	}

	public Object jsFunction_getHeaders(String name) {
		return headersToJS(response.getRawResponse().getHeaders(name));
	}

	public Object jsGet_allHeaders() {
		return headersToJS(response.getRawResponse().getAllHeaders());
	}

	public Object jsFunction_getCookies() {
		return cookiesToJS(response.getResponseCookies());
	}
	public Object jsGet_cookies() {
		return cookiesToJS(response.getResponseCookies());
	}

	private Object headersToJS(Header[] headers) {
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		final Context cx = Context.getCurrentContext();
		final Scriptable array = cx.newArray(scope, headers.length);
		for(int i = 0; i < headers.length; i++) {
			array.put(i, array, Context.javaToJS(headers[i], scope));
		}
		return array;
	}
	
	private Object cookiesToJS(List<IHttpResponseCookie> cookies) {
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		final Context cx = Context.getCurrentContext();
		final Scriptable array = cx.newArray(scope, cookies.size());
		for(int i = 0; i < cookies.size(); i++) {
			array.put(i, array, Context.javaToJS(cookies.get(i), scope));
		}
		return array;
	}

	public boolean jsGet_fetchFail() {
		return response.isFetchFail();
	}

	public Scriptable jsGet_document() {
		if(cachedDocument == null) {
			cachedDocument = createCachedDocument();
		}
		return cachedDocument;
	}

	public Object jsGet_originalRequest() {
		return export(response.getOriginalRequest());
	}

	public Object jsGet_rawResponse() {
		return export(response.getRawResponse());
	}

	public Object jsGet_host() {
		return export(response.getHost());
	}

	public String jsGet_bodyAsString() {
		return response.getBodyAsString();
	}

	public boolean jsGet_mostlyAscii() {
		return response.isMostlyAscii();
	}

	public Object jsGet_fingerprint() {
		return export(response.getPageFingerprint());
	}

	public int jsGet_milliseconds() {
		return (int) response.getRequestMilliseconds();
	}

	private Object export(Object ob) {
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		return Context.javaToJS(ob, scope);
	}

	@Override
	public String getClassName() {
		return "Response";
	}
}
