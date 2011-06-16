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
package com.subgraph.vega.internal.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.IModelProperties;

public class ModelProperties implements IModelProperties, Activatable {
	
	private final Map<String, Object> propertyMap;
	private transient Activator activator;
	
	public ModelProperties() {
		propertyMap = new ActivatableHashMap<String, Object>();
	}
	
	public void setProperty(String name, Object value) {
		activate(ActivationPurpose.READ);
		synchronized(propertyMap) {
			propertyMap.put(name, value);
		}		
	}
	
	public void setStringProperty(String name, String value) {
		setProperty(name, value);		
	}
	
	public void setIntegerProperty(String name, int value) {
		setProperty(name, value);		
	}
	
	public Object getProperty(String name) {
		activate(ActivationPurpose.READ);
		synchronized(propertyMap) {
			return propertyMap.get(name);
		}
	}
	
	public String getStringProperty(String name) {
		final Object value = getProperty(name);
		if(value == null)
			return null;
		if(value instanceof String)
			return (String) value;
		throw new IllegalArgumentException("Property '"+ name +"' exists but it is not a String");
	}
	
	public Integer getIntegerProperty(String name) {
		final Object value = getProperty(name);
		if(value == null)
			return null;
		if(value instanceof Integer)
			return (Integer) value;
		throw new IllegalArgumentException("Property '"+ name +"' exists but it is not an Integer");
	}
	
	public List<String> propertyKeys() {
		activate(ActivationPurpose.READ);
		synchronized(propertyMap) {
			return new ArrayList<String>(propertyMap.keySet());
		}
	}
	
	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}		
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator)
			return;
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;			
	}
}
