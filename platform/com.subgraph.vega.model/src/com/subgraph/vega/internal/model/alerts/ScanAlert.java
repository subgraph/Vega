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
package com.subgraph.vega.internal.model.alerts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertHighlight;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.internal.model.ModelProperties;

public class ScanAlert implements IScanAlert, Activatable {
	
	private final String name;
	private final Severity severity;
	private final String title;
	private final String key;
	private final IScanInstance scanInstance;
	private final long requestId;
	private final List<IScanAlertHighlight> alertHighlights;
	private String templateName = "main";
	private String resource;
	private final ModelProperties properties;
	
	private transient Activator activator;
	
	ScanAlert(String key, String name, String title, Severity severity, IScanInstance scanInstance, long requestId) {
		this.key = key;
		this.name = name;
		this.title = title;
		this.severity = severity;
		this.properties = new ModelProperties();
		this.scanInstance = scanInstance;
		this.requestId = requestId;
		this.alertHighlights = new ActivatableArrayList<IScanAlertHighlight>();
	}
	
	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}
	
	@Override
	public Severity getSeverity() {
		activate(ActivationPurpose.READ);
		return severity;
	}

	@Override
	public String getTitle() {
		activate(ActivationPurpose.READ);
		return title;
	}

	@Override
	public void setTemplateName(String name) {
		activate(ActivationPurpose.READ);
		templateName = name;		
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void setProperty(String name, Object value) {
		activate(ActivationPurpose.READ);
		if(name.equals("resource") && value instanceof String) {
			resource = (String) value;
			activate(ActivationPurpose.WRITE);
		}
		properties.setProperty(name, value);
	}

	@Override
	public void setStringProperty(String name, String value) {
		activate(ActivationPurpose.READ);
		properties.setStringProperty(name, value);
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		activate(ActivationPurpose.READ);
		properties.setIntegerProperty(name, value);
	}

	@Override
	public Object getProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getProperty(name);
	}

	@Override
	public String getStringProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getStringProperty(name);
	}

	@Override
	public Integer getIntegerProperty(String name) {
		activate(ActivationPurpose.READ);
		return properties.getIntegerProperty(name);
	}

	@Override
	public List<String> propertyKeys() {
		activate(ActivationPurpose.READ);
		return properties.propertyKeys();
	}

	@Override
	public String getTemplateName() {
		activate(ActivationPurpose.READ);
		return templateName;
	}
	
	@Override
	public String getResource() {
		activate(ActivationPurpose.READ);
		return resource;
	}

	@Override
	public String getKey() {
		activate(ActivationPurpose.READ);
		return key;
	}

	@Override
	public boolean hasAssociatedRequest() {
		activate(ActivationPurpose.READ);
		return requestId != -1;
	}

	@Override
	public IScanInstance getScanInstance() {
		activate(ActivationPurpose.READ);
		return scanInstance;
	}
	
	@Override
	public long getScanId() {
		activate(ActivationPurpose.READ);
		return scanInstance.getScanId();
	}

	@Override
	public long getRequestId() {
		activate(ActivationPurpose.READ);
		return requestId;
	}
	
	@Override
	public void addStringMatchHighlight(String matchStr) {
		activate(ActivationPurpose.READ);
		alertHighlights.add(new ScanAlertHighlight(matchStr, false, false));
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void addRegexHighlight(String regex) {
		activate(ActivationPurpose.READ);
		alertHighlights.add(new ScanAlertHighlight(regex, true, true));
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public void addRegexCaseInsensitiveHighlight(String regex) {
		activate(ActivationPurpose.READ);
		alertHighlights.add(new ScanAlertHighlight(regex, true, false));
		activate(ActivationPurpose.WRITE);
	
	}
	@Override
	public Collection<IScanAlertHighlight> getHighlights() {
		activate(ActivationPurpose.READ);
		return Collections.unmodifiableCollection(new ArrayList<IScanAlertHighlight>(alertHighlights));
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;			
	}


}
