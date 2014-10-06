/*******************************************************************************
 * Copyright (c) 2013 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

import com.google.common.net.InternetDomainName;
import com.google.common.io.BaseEncoding;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;
import com.subgraph.vega.api.scanner.IPathState;

public class ModuleContextJS {

	private final Scriptable scope;
	private final IInjectionModuleContext context;

	ModuleContextJS(Scriptable scope, IInjectionModuleContext context) {
		this.scope = scope;
		this.context = context;
	}

	public int getCurrentIndex() {	
		return context.getCurrentIndex();
	}

	public IPathState getPathState() {
		return context.getPathState();
	}

	public int incrementResponseCount() {
		return context.incrementResponseCount();
	}

	public boolean allResponsesReceived() {
		return context.allResponsesReceived();
	}

	public void setModuleFailed() {
		context.setModuleFailed();
	}
	
	public boolean hasModuleFailed() {
		return context.hasModuleFailed();
	}

	public void addRequestResponse(HttpUriRequest request,
			ResponseJS response) {
		context.addRequestResponse(request, response.getResponse());
	}

	public void addRequestResponse(int index, HttpUriRequest request,
			ResponseJS response) {
		context.addRequestResponse(index, request, response.getResponse());
	}

	public HttpUriRequest getSavedRequest(int index) {
		return context.getSavedRequest(index);
	}

	public Scriptable getSavedResponse(int index) {
		final IHttpResponse r = context.getSavedResponse(index);
		Object responseOb = Context.javaToJS(r, scope);
		Object[] args = { responseOb };
		return Context.getCurrentContext().newObject(scope, "Response", args);
	}
	
	public Scriptable getOrigResponse() {
		final IHttpResponse r =  context.getPathState().getResponse();
	    Object responseOb = Context.javaToJS(r, scope);
		Object[] args = { responseOb };
		return Context.getCurrentContext().newObject(scope, "Response", args);		
	}
	
	/* Added below method because of bug #547 */

	public String getSavedResponseBody(int index) {
		return context.getSavedResponseBody(index);

	}

	public IPageFingerprint getSavedFingerprint(int index) {
		return context.getSavedFingerprint(index);
	}

	public void error(HttpUriRequest request, ResponseJS response,
			String message) {
		context.error(request, response.getResponse(), message);
	}

	public void debug(String msg) {
		context.debug(msg);
	}

	public void debug(String msg, Boolean flag) {
		context.debug(msg, flag);
	}
	
	public void analyzePage(HttpUriRequest request, ResponseJS response) {
		context.analyzePage(request, response.getResponse());
	}

	public boolean isFingerprintMatch(int idx1, int idx2) {
		return context.isFingerprintMatch(idx1, idx2);
	}

	public boolean isFingerprintMatch(int idx, IPageFingerprint fp) {
		return context.isFingerprintMatch(idx, fp);
	}

	public void submitRequest(HttpUriRequest request,
			Function callback, int index) {
		context.submitRequest(request, wrap(callback), index);
	}

	public void submitRequest(HttpUriRequest request,
			Function callback) {
		context.submitRequest(request, wrap(callback));
	}

	public void submitRequest(Function callback, int flag) {
		context.submitRequest(wrap(callback), flag);
	}

	public void submitAlteredRequest(Function callback,
			String value) {
		context.submitAlteredRequest(wrap(callback), value);
	}

	public void submitAlteredRequest(Function callback,
			String value, int flag) {
		context.submitAlteredRequest(wrap(callback), value, flag);
	}

	public void submitAlteredRequest(Function callback,
			String value, boolean append, int flag) {
		context.submitAlteredRequest(wrap(callback), value, append, flag);
	}
	
	public void submitAlteredParameterNameRequest(
			Function callback, String name, int flag) {
		context.submitAlteredParameterNameRequest(wrap(callback), name, flag);
	}

	public void submitMultipleAlteredRequests(Function callback, String[] injectables) {
		context.submitMultipleAlteredRequests(wrap(callback), injectables);
	}

	public void submitMultipleAlteredRequests(Function callback, String[] injectables, boolean append) {
		context.submitMultipleAlteredRequests(wrap(callback), injectables, append);
	}
	
	public void responseChecks(HttpUriRequest request, ResponseJS response) {
		context.responseChecks(request, response.getResponse());
	}
	
	public void contentChecks(HttpUriRequest request, ResponseJS response) {
		context.contentChecks(request, response.getResponse());
	}
	
	public void responseChecks(int idx) {
		context.responseChecks(idx);
	}
	
	public void pivotChecks(HttpUriRequest request, ResponseJS response) {
		context.pivotChecks(request, response.getResponse());
	}

	private CrawlerCallbackWrapper wrap(Function callback) {
		return new CrawlerCallbackWrapper(callback);
	}
	
	public void addStringHighlight(String str) {
		context.addStringHighlight(str);
	}

	public void addRegexHighlight(String regex) {
		context.addRegexHighlight(regex);
	}

	public void addRegexCaseInsensitiveHighlight(String regex) {
		context.addRegexCaseInsensitiveHighlight(regex);
	}
	
	public void reset () {
		context.reset();
	}

	public void alert(String type, HttpUriRequest request, ResponseJS response) {
		alert(type, request, response, null);
	}
	
	public void alert(String type, HttpRequest request, ResponseJS response, Scriptable ob) {
		List<Object> properties = new ArrayList<Object>();
		String keyValue = null;
		String messageValue = null;
		if(ob == null) {
			publishAlert(type, null, request, response.getResponse());
			return;
		}

		for(Object k: ob.getIds()) {
			if(k instanceof String) {
				String key = (String) k;
				String val = lookup(key, ob);
				if(val != null) {
					if("key".equals(key)) {
						keyValue = val;
					} else if("message".equals(key)) {
						messageValue = val;
					} else {
						properties.add(key);
						properties.add(val);
					}
				}
			}
		}
		context.publishAlert(type, keyValue, messageValue, request, response.getResponse(), properties.toArray());		
	}

	private String lookup(String key, Scriptable ob) {
		final Object value = ob.get(key, ob);
		if(value instanceof String) {
			return (String) value;
		} else if(value instanceof Wrapper) {
			Wrapper w = (Wrapper) value;
			if(w.unwrap() instanceof String) {
				return (String) w.unwrap();
			}
			return null;
		} else {
			return null;
		}
	}
	
	public void publishAlert(String type, String message, HttpRequest request, IHttpResponse response) {
		context.publishAlert(type, message, request, response);
	}
	
	public void publishAlert(String type, String key, String message, HttpRequest request, IHttpResponse response) {
		context.publishAlert(type, key, message, request, response);
	}


	public void setProperty(String name, Object value) {
		context.setProperty(name, value);
	}

	public void setStringProperty(String name, String value) {
		context.setStringProperty(name, value);		
	}

	public void setIntegerProperty(String name, int value) {
		context.setIntegerProperty(name, value);		
	}


	public Object getProperty(String name) {
		return context.getProperty(name);
	}


	public String getStringProperty(String name) {
		return context.getStringProperty(name);
	}

	public Integer getIntegerProperty(String name) {
		return context.getIntegerProperty(name);
	}

	public Object propertyKeys() {
				
		List<String> keys = context.propertyKeys();
		
		final Context cx = Context.getCurrentContext();
		final Scriptable array = cx.newArray(scope, keys.size());
		for(int i = 0; i < keys.size(); i++) {
			array.put(i, array, Context.javaToJS(keys.get(i), scope));
		}
		return array;
		
	}
	
	public boolean alertExists(String key) {
		return context.alertExists(key);
	}
	
	public InternetDomainName internetDomainName(String domain) {
		return context.internetDomainName(domain);
	}
	
	public boolean isValidInternetDomainName(String domain) {
		return context.isValidInternetDomainName(domain);
	}
	
	public String base64decode(String encoded) {
		byte[] decoded = BaseEncoding.base64().decode(encoded);
		return new String(decoded);
	}
	
	public String base64encode(String input) {
		String encoded = BaseEncoding.base64().encode(input.getBytes());
		return encoded;
	}
}