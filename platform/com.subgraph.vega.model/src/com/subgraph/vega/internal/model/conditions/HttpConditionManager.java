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
package com.subgraph.vega.internal.model.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.subgraph.vega.api.events.NamedEventListenerManager;
import com.subgraph.vega.api.model.conditions.ConditionSetChanged;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public class HttpConditionManager implements IHttpConditionManager {

	private final ObjectContainer database;
	private final List<IHttpConditionType> conditionTypes;
	private final NamedEventListenerManager conditionSetChangedManager;

	public HttpConditionManager(ObjectContainer database, NamedEventListenerManager conditionSetChangedManager) {
		this.database = database;
		this.conditionTypes = createConditionTypes();
		this.conditionSetChangedManager = conditionSetChangedManager;
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(database);
		registry.activating().addListener(new EventListener4<CancellableObjectEventArgs>() {
			@Override
			public void onEvent(Event4<CancellableObjectEventArgs> e, CancellableObjectEventArgs args) {
				final Object ob = args.object();
				if(ob instanceof HttpConditionSet) 
					((HttpConditionSet)ob).setConditionManager(HttpConditionManager.this);					
				else if(ob instanceof HttpConditionSetMap) 
					((HttpConditionSetMap)ob).setConditionManager(HttpConditionManager.this);
			}
		});
		notifyAllChanged();
	}

	private List<IHttpConditionType> createConditionTypes() {
		final List<IHttpConditionType> types = new ArrayList<IHttpConditionType>();
		types.add(ConditionHostname.getConditionType());
		types.add(ConditionRequestMethod.getConditionType());
		types.add(ConditionHeader.getRequestConditionType());
		types.add(ConditionPath.getConditionType());
		types.add(ConditionHeader.getResponseConditionType());
		types.add(ConditionResponseLength.getConditionType());
		types.add(ConditionResponseStatusCode.getConditionType());
		types.add(ConditionRequestId.getConditionType());
		return types;
	}
	
	public void notifyClosed() {
		synchronized(conditionSetChangedManager) {
			conditionSetChangedManager.fireAllKeys(new ConditionSetChanged(null));
		}
	}
	
	private void notifyAllChanged() {
		synchronized(conditionSetChangedManager) {
			for(String conditionSetName: conditionSetChangedManager.getAllKeys()) {
				IHttpConditionSet conditionSet = getConditionSet(conditionSetName);
				conditionSetChangedManager.fireEvent(conditionSetName, new ConditionSetChanged(conditionSet));
			}
		}
	}

	void notifyConditionSetChanged(IHttpConditionSet conditionSet) {
		conditionSetChangedManager.fireEvent(conditionSet.getName(), new ConditionSetChanged(conditionSet));
	}

	@Override
	public IHttpConditionSet getConditionSet(String name) {
		return getConditionSetMap().getConditionSet(name);
	}
	
	@Override
	public IHttpConditionSet getConditionSetCopy(String name) {
		return getConditionSetMap().getConditionSetCopy(name);
	}

	@Override
	public void saveConditionSet(String name, IHttpConditionSet conditionSet) {
		synchronized(conditionSetChangedManager) {
			getConditionSetMap().saveConditionSet(name, conditionSet);
			conditionSetChangedManager.fireEvent(name, new ConditionSetChanged(conditionSet));
		}
	}

	private HttpConditionSetMap getConditionSetMap() {
		synchronized(this) {
			final List<HttpConditionSetMap> result = database.query(HttpConditionSetMap.class);
			if(result.size() == 1)
				return result.get(0);
			else if(result.size() > 1)
				throw new IllegalStateException("Duplicate HttpConditionSetMap records in database");
			HttpConditionSetMap map = new HttpConditionSetMap(this);
			database.store(map);
			return map;
		}
	}

	@Override
	public List<IHttpConditionType> getConditionTypes() {
		return getConditionTypes(false);
	}
	
	@Override
	public List<IHttpConditionType> getConditionTypes(boolean includeInternal) {
		if(includeInternal) {
			return Collections.unmodifiableList(new ArrayList<IHttpConditionType>(conditionTypes));
		}
		final List<IHttpConditionType> result = new ArrayList<IHttpConditionType>();
		for(IHttpConditionType type: conditionTypes) {
			if(!type.isInternal()) {
				result.add(type);
			}
		}
		return Collections.unmodifiableList(result);
		
	}

	@Override
	public IHttpConditionType getConditionTypeByName(String name) {
		for(IHttpConditionType type: getConditionTypes(true)) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;	
	}
}
