package com.subgraph.vega.impl.scanner.modules.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
	
	ResponseModuleContext(IWorkspace workspace, IScanInstance scanInstance) {
		this.workspace = workspace;
		this.scanInstance = scanInstance;
	}

	@Override
	public void error(HttpUriRequest request, IHttpResponse response,
			String message) {
		final long requestId = workspace.getRequestLog().addRequestResponse(response.getOriginalRequest(), response.getRawResponse(), response.getHost(), response.getRequestMilliseconds());
		logger.warning("Error running module: "+ message + " (request logged with id="+ requestId +")");
	}

	@Override
	public void debug(String msg) {
		logger.info(msg);
	}
	
	public void alert(String type, HttpUriRequest request, IHttpResponse response) {
		alert(type, request, response, null);
	}
	
	public void alert(String type, HttpRequest request, IHttpResponse response, Scriptable ob) {
		List<Object> properties = new ArrayList<Object>();
		String keyValue = null;
		String messageValue = null;
		if(ob == null) {
			publishAlert(type, null, request, response);
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
		publishAlert(type, keyValue, messageValue, request, response, properties.toArray());
		
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
		debug("Publishing Alert: ("+ type +") ["+ request.getRequestLine().getUri() +"] "+ message);
		final IRequestLog requestLog = workspace.getRequestLog();
		
		try {
			scanInstance.lock();
			if(key != null && scanInstance.hasAlertKey(key)) {
				return;
			}
			final long requestId = requestLog.addRequestResponse(response.getOriginalRequest(), response.getRawResponse(), response.getHost(), response.getRequestMilliseconds());
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
			scanInstance.addAlert(alert);
		} finally {
			scanInstance.unlock();
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
}
