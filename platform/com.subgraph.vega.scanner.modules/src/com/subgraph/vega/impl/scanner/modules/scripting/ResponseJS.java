package com.subgraph.vega.impl.scanner.modules.scripting;


import org.apache.http.Header;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.impl.scanner.modules.scripting.dom.HTMLDocumentJS;

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
		this.cachedDocument = createCachedDocument();
	}
	
	
	private Scriptable createCachedDocument() {
		final IHTMLParseResult htmlResult = response.getParsedHTML();
		if(htmlResult == null) {
			return null;
		}
		final HTMLDocument domDocument = htmlResult.getDOMDocument();
		final HTMLDocumentJS jsDocument = new HTMLDocumentJS(domDocument);
		jsDocument.setParentScope(getParentScope());
		jsDocument.setPrototype(getPrototype());
		return jsDocument;
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

	private Object headersToJS(Header[] headers) {
		final Scriptable scope = ScriptableObject.getTopLevelScope(this);
		final Object[] hdrObjects = new Scriptable[headers.length];
		final Context cx = Context.getCurrentContext();
		for(int i = 0; i < headers.length; i++) {
			hdrObjects[i] = Context.javaToJS(headers[i], scope);
		}
		return cx.newArray(scope, hdrObjects);		
	}

	public boolean jsGet_fetchFail() {
		return response.isFetchFail();
	}

	public Scriptable jsGet_document() {
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
