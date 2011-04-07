package com.subgraph.vega.internal.model.conditions;

import java.util.Map;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public class HttpConditionSetMap implements Activatable {

	private final Map<String, IHttpConditionSet> conditionSetMap = new ActivatableHashMap<String, IHttpConditionSet>();
	
	private transient Activator activator;
	private transient IHttpConditionManager conditionManager;
	
	public HttpConditionSetMap(HttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}
	
	void setConditionManager(IHttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}

	IHttpConditionSet getConditionSet(String name) {
		activate(ActivationPurpose.READ);
		synchronized(conditionSetMap) {
			if(!conditionSetMap.containsKey(name))
				conditionSetMap.put(name, new HttpConditionSet(conditionManager));
			return conditionSetMap.get(name);
		}
	}
	
	IHttpConditionSet getConditionSetCopy(String name) {
		activate(ActivationPurpose.READ);
		synchronized(conditionSetMap) {
			return new HttpConditionSet(conditionManager, conditionSetMap.get(name));
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
