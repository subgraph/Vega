package com.subgraph.vega.internal.model.conditions;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public class HttpConditionSet implements IHttpConditionSet, Activatable {

	private final List<IHttpCondition> conditionList = new ActivatableArrayList<IHttpCondition>();
	private transient IHttpConditionManager conditionManager;
	
	HttpConditionSet(IHttpConditionManager conditionManager) {
		this(conditionManager, null);
	}
	
	HttpConditionSet(IHttpConditionManager conditionManager, IHttpConditionSet copyMe) {
		this.conditionManager = conditionManager;
		if(copyMe != null) {
			for(IHttpCondition c: copyMe.getAllConditions()) 
				conditionList.add(c.createCopy());
		}
	}

	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		return matchesAllConditions(request, response);
	}

	private boolean matchesAllConditions(HttpRequest request, HttpResponse response) {
		synchronized(conditionList) {
			for(IHttpCondition c: conditionList) {
				if(c.isEnabled() && !c.matches(request, response))
					return false;
			}
			return true;
		}
	}
	
	@SuppressWarnings("unused")
	private boolean matchesAnyCondition(HttpRequest request, HttpResponse response) {
		synchronized(conditionList) {
			for(IHttpCondition c: conditionList)
				if(c.isEnabled() && c.matches(request, response))
					return true;
			return false;
		}
		
	}
	@Override
	public void appendCondition(IHttpCondition condition) {
		activate(ActivationPurpose.READ);
		conditionList.add(condition);
	}

	@Override
	public void removeCondition(IHttpCondition condition) {
		activate(ActivationPurpose.READ);
		conditionList.remove(condition);
	}

	@Override
	public List<IHttpCondition> getAllConditions() {
		activate(ActivationPurpose.READ);
		return new ArrayList<IHttpCondition>(conditionList);
	}

	@Override
	public IHttpConditionManager getConditionManager() {
		return conditionManager;
	}

	void setConditionManager(IHttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}

	private transient Activator activator;

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
