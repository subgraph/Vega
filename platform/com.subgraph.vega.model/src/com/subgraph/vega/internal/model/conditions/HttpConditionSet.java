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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.query.Query;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpConditionSet implements IHttpConditionSet, Activatable {

	private final String name;
	private final List<IHttpCondition> conditionList = new ActivatableArrayList<IHttpCondition>();
	private boolean matchOnEmptySet;
	private transient HttpConditionManager conditionManager;
	
	HttpConditionSet(String name, HttpConditionManager conditionManager) {
		this(name, conditionManager, null);
	}
	
	HttpConditionSet(String name, HttpConditionManager conditionManager, IHttpConditionSet copyMe) {
		this.name = name;
		this.conditionManager = conditionManager;
		if(copyMe != null) {
			for(IHttpCondition c: copyMe.getAllConditions()) 
				conditionList.add(c.createCopy());
		}
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public boolean matchesAll(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		return matchesAllConditions(request, response);
	}

	@Override
	public boolean matchesAny(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		return matchesAnyCondition(request, response);
	}
	
	private boolean matchesAllConditions(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		synchronized(conditionList) {
			if (conditionList.size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: conditionList) {
				if(c.isEnabled() && !c.matches(request, response))
					return false;
			}
			return true;
		}
	}
	
	private boolean matchesAnyCondition(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		synchronized(conditionList) {
			if (conditionList.size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: conditionList) {
				if(c.isEnabled() && c.matches(request, response)) {
					return true;
				}
			}
			return false;
		}
		
	}
	@Override
	public void appendCondition(IHttpCondition condition) {
		activate(ActivationPurpose.READ);
		conditionList.add(condition);
		conditionManager.notifyConditionSetChanged(this);
	}

	@Override
	public void removeCondition(IHttpCondition condition) {
		activate(ActivationPurpose.READ);
		conditionList.remove(condition);
		conditionManager.notifyConditionSetChanged(this);
	}

	@Override
	public void clearConditions() {
		activate(ActivationPurpose.READ);
		conditionList.clear();
		conditionManager.notifyConditionSetChanged(this);
	}

	@Override
	public List<IHttpCondition> getAllConditions() {
		activate(ActivationPurpose.READ);
		return new ArrayList<IHttpCondition>(conditionList);
	}

	@Override
	public IHttpConditionManager getConditionManager() {
		activate(ActivationPurpose.READ);
		return conditionManager;
	}

	void setConditionManager(HttpConditionManager conditionManager) {
		activate(ActivationPurpose.READ);
		this.conditionManager = conditionManager;
		activate(ActivationPurpose.WRITE);
	}
	
	public List<IRequestLogRecord> filterRequestLog(ObjectContainer db) {
		activate(ActivationPurpose.READ);
		if(!hasRecords(db)) {
			return Collections.emptyList();
		}
		final Query query = db.query();
		query.constrain(IRequestLogRecord.class);

		for(IHttpCondition c: conditionList) {
			((AbstractCondition) c).filterRequestLogQuery(query);
		}
		return query.execute();
	}

	private boolean hasRecords(ObjectContainer db) {
		final Query query = db.query();
		query.constrain(IRequestLogRecord.class);
		return query.execute().size() > 0;
	}

	@Override
	public void setMatchOnEmptySet(boolean flag) {
		activate(ActivationPurpose.READ);
		matchOnEmptySet = flag;
		activate(ActivationPurpose.WRITE);
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
