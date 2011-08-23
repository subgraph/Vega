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
package com.subgraph.vega.internal.model.web;

import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.IModelProperties;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.NewWebEntityEvent;
import com.subgraph.vega.api.model.web.UpdatedWebEntityEvent;
import com.subgraph.vega.internal.model.ModelProperties;

public abstract class WebEntity implements IWebEntity, Activatable {
	
	private transient Activator activator;
	protected transient EventListenerManager eventManager;
	protected transient ObjectContainer database;
	
	private boolean visitedFlag;
	private boolean scannedFlag;
	
	private IModelProperties properties = new ModelProperties();
	
	protected WebEntity(EventListenerManager eventManager, ObjectContainer database) {
		this.eventManager = eventManager;
		this.database = database;
	}
	
	@Override
	public boolean isVisited() {
		activate(ActivationPurpose.READ);
		return visitedFlag;
	}

	@Override
	public void setVisited(boolean notify) {
		activate(ActivationPurpose.READ);
		if(getParent() != null)
			getParent().setVisited(notify);
		if(!visitedFlag) {
			visitedFlag = true;
			activate(ActivationPurpose.WRITE);
			if(notify)
				notifyUpdatedEntity(this);
		}		
	}
	
	@Override
	public boolean isScanned() {
		activate(ActivationPurpose.READ);
		return scannedFlag;
	}
	
	@Override
	public void setScanned() {
		activate(ActivationPurpose.READ);
		scannedFlag = true;
		activate(ActivationPurpose.WRITE);
	}
	
	@Override
	public void setProperty(String name, Object value) {
		activate(ActivationPurpose.READ);
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
	
	void setEventManager(EventListenerManager eventManager) {
		this.eventManager = eventManager;
	}
	
	void setDatabase(ObjectContainer database) {
		if(this.database == null)
			this.database = database;
	}
	
	ObjectContainer getDatabase() {
		return database;
	}
	
	void notifyNewEntity(IWebEntity entity) {
		if(eventManager != null)
			eventManager.fireEvent(new NewWebEntityEvent(entity));
	}
	
	void notifyUpdatedEntity(IWebEntity entity) {
		if(eventManager != null)
			eventManager.fireEvent(new UpdatedWebEntityEvent(entity));
	}
}
