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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.scanner.IModuleContext;

public class ResponseModuleContext implements IModuleContext {
	private final static Logger logger = Logger.getLogger("modules");

	private final IWorkspace workspace;
	private final IScanInstance scanInstance;
	private final List<String> stringHighlights;
	private final List<String> regexHighlights;
	private final List<String> regexCaseInsensitiveHighlights;
	
	ResponseModuleContext(IWorkspace workspace, IScanInstance scanInstance) {
		this.workspace = workspace;
		this.scanInstance = scanInstance;
		this.stringHighlights = new ArrayList<String>();
		this.regexHighlights = new ArrayList<String>();
		this.regexCaseInsensitiveHighlights = new ArrayList<String>();
	}

	@Override
	public void error(HttpUriRequest request, IHttpResponse response,
			String message) {
		final long requestId = workspace.getRequestLog().addRequestResponse(response);
		logger.warning("Error running module: "+ message + " (request logged with id="+ requestId +")");
	}

	public void error(HttpUriRequest request, ResponseJS response, String message) {
		error(request, response.getResponse(), message);
	}

	@Override
	public void debug(String msg) {
		workspace.consoleWrite(msg);
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
		publishAlert(type, keyValue, messageValue, request, response.getResponse(), properties.toArray());
		
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

	@Override
	public void publishAlert(String type, String message,
			HttpRequest request, IHttpResponse response,
			Object... properties) {
		publishAlert(type, null, message, request, response, properties);
	}

	@Override
	public void publishAlert(String type, String key, String message,
			HttpRequest request, IHttpResponse response,
			Object... properties) {
		debug("Publishing Alert: ("+ type +") ["+ request.getRequestLine().getUri() +"] ");
		final IRequestLog requestLog = workspace.getRequestLog();
		
		synchronized(scanInstance) {
			if(key != null && scanInstance.hasAlertKey(key)) {
				return;
			}
			final long requestId = requestLog.addRequestResponse(response);
			final IScanAlert alert = scanInstance.createAlert(type, key, requestId);
			
			for(int i = 0; (i + 1) < properties.length; i += 2) {
				if(properties[i] instanceof String) {
					alert.setProperty((String) properties[i], properties[i + 1]);
				} else {
					logger.warning("Property key passed to publishAlert() is not a string");
				}
			}
			
			if(message != null) {
				alert.setStringProperty("message", message);
			}
			for(String hl: stringHighlights) {
				alert.addStringMatchHighlight(hl);
			}
			for(String hl: regexHighlights) {
				alert.addRegexHighlight(hl);
			}
			for (String hl: regexCaseInsensitiveHighlights) {
				alert.addRegexCaseInsensitiveHighlight(hl);
			}
			scanInstance.addAlert(alert);
		}
	}

	@Override
	public void setProperty(String name, Object value) {
		scanInstance.setProperty(name, value);
	}

	@Override
	public void setStringProperty(String name, String value) {
		scanInstance.setStringProperty(name, value);
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		scanInstance.setIntegerProperty(name, value);
	}

	@Override
	public Object getProperty(String name) {
		return scanInstance.getProperty(name);
	}

	@Override
	public String getStringProperty(String name) {
		return scanInstance.getStringProperty(name);
	}

	@Override
	public Integer getIntegerProperty(String name) {
		return scanInstance.getIntegerProperty(name);
	}

	@Override
	public List<String> propertyKeys() {
		return scanInstance.propertyKeys();
	}

	@Override
	public void addStringHighlight(String str) {
		stringHighlights.add(str);
	}

	@Override
	public void addRegexHighlight(String regex) {
		try {
			Pattern.compile(regex);
			regexHighlights.add(regex);
		} catch (PatternSyntaxException e) {
			logger.warning("Invalid regular expression '"+ regex +"' passed to addHighlightRegex(): "+ e.getDescription());
		}
	}
	
	@Override 
	public void addRegexCaseInsensitiveHighlight(String regex) {
		try {
			Pattern.compile(regex);
			regexCaseInsensitiveHighlights.add(regex);
		} catch (PatternSyntaxException e) {
			logger.warning("Invalid regular expression '"+ regex +"' passed to addRegexCaseInsensitiveHighlights(): "+ e.getDescription());
		}
	}
		
	
	@Override
	public void reset() {
		stringHighlights.clear();
		regexHighlights.clear();
	}
}
