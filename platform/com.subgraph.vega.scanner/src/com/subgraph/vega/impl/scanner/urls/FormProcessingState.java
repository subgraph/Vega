package com.subgraph.vega.impl.scanner.urls;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class FormProcessingState {
	private final static Logger logger = Logger.getLogger("scanner");
	private final static FormHints formHints = new FormHints();
	private final URI baseURI;
	private final String action;
	private final String method;

	private final List<NameValuePair> parameters = new ArrayList<NameValuePair>();

	private URI cachedTargetURI;
	private boolean passwordFlag;
	private boolean fileFieldFlag;

	FormProcessingState(URI baseURI, String action, String method) {
		this.baseURI = baseURI;
		this.action = action;
		this.method = method;
	}

	boolean isValid() {
		return (getTargetURI() != null);
	}

	boolean isPostMethod() {
		return (method != null && method.toLowerCase().equals("post"));
	}

	URI getTargetURI() {
		synchronized(this) {
			if(cachedTargetURI == null)
				cachedTargetURI = createTargetURI();
			return cachedTargetURI;
		}
	}

	private URI createTargetURI() {
		if(baseURI == null)
			return null;
		if(action == null)
			return baseURI;
		try {
			final URI target = baseURI.resolve(action);
			final String scheme = target.getScheme();
			if(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
				return target;
			else
				return null;
		} catch (IllegalArgumentException e) {
			logger.log(Level.WARNING, "Failed to create new URI from base: "+ baseURI + " and action="+ action, e);
			return null;
		}
	}
	void add(String name, String value) {
		parameters.add(new BasicNameValuePair(name, (value == null) ? ("") : (value)));
	}

	void addGuessedValue(String name) {
		add(name, guessFormValue(name));
	}
	void setPasswordFieldFlag() {
		passwordFlag = true;
	}

	boolean getPasswordFieldFlag() {
		return passwordFlag;
	}

	void setFileFieldFlag() {
		fileFieldFlag = true;
	}

	boolean getFileFieldFlag() {
		return fileFieldFlag;
	}

	private String guessFormValue(String name) {
		return formHints.lookupHint(name);
	}

	List<NameValuePair> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		if(isPostMethod()) {
			return "POST "+ getTargetURI().toString() + parametersAsPostString();
		} else {
			return "GET " + getTargetURI().toString() + parametersAsQueryString();
		}
	}

	private String parametersAsQueryString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("?");
		for(NameValuePair nvp: parameters) {
			if(sb.length() > 1)
				sb.append("&");
			sb.append(nvp.getName());
			if(nvp.getValue() != null) {
				sb.append("=");
				sb.append(nvp.getValue());
			}
		}
		return sb.toString();
	}

	private String parametersAsPostString() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append(" [");
		for(NameValuePair nvp: parameters) {
			if(first)
				first = false;
			else
				sb.append(", ");
			sb.append(nvp.getName());
			if(nvp.getValue() != null) {
				sb.append("=");
				sb.append(nvp.getValue());
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
