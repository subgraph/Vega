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

import java.util.Map;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public class HttpConditionSetMap implements Activatable {

	private final Map<String, IHttpConditionSet> conditionSetMap = new ActivatableHashMap<String, IHttpConditionSet>();
	
	private transient Activator activator;
	private transient HttpConditionManager conditionManager;
	
	public HttpConditionSetMap(HttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}
	
	void setConditionManager(HttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}

	IHttpConditionSet getConditionSet(String name) {
		activate(ActivationPurpose.READ);
		synchronized(conditionSetMap) {
			if(!conditionSetMap.containsKey(name))
				conditionSetMap.put(name, new HttpConditionSet(name, conditionManager));
			return conditionSetMap.get(name);
		}
	}
	
	IHttpConditionSet getConditionSetCopy(String name) {
		activate(ActivationPurpose.READ);
		synchronized(conditionSetMap) {
			return new HttpConditionSet(name, conditionManager, conditionSetMap.get(name));
		}
	}

	void saveConditionSet(String name, IHttpConditionSet conditionSet) {
		activate(ActivationPurpose.READ);
		synchronized (conditionSet) {
			conditionSetMap.put(name, conditionSet);
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
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;			
	}

}
