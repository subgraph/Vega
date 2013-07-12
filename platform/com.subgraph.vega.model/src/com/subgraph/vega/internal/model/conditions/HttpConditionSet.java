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
import com.db4o.ObjectSet;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.query.Constraint;
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
	private transient Object lock;
	private transient HttpConditionManager conditionManager;
	private transient List<IHttpCondition> temporaryConditions;
	private transient List<IHttpCondition> allConditions;
	
	HttpConditionSet(String name, HttpConditionManager conditionManager) {
		this(name, conditionManager, null);
	}
	
	HttpConditionSet(String name, HttpConditionManager conditionManager, IHttpConditionSet copyMe) {
		this.name = name;
		this.conditionManager = conditionManager;
		this.temporaryConditions = new ArrayList<IHttpCondition>();
		this.lock = new Object();
		if(copyMe != null) {
			for(IHttpCondition c: copyMe.getAllConditions(true)) { 
				conditionList.add(c.createCopy());
			}
			for(IHttpCondition c: copyMe.getAllTemporaryConditions(true)) {
				temporaryConditions.add(c.createCopy());
			}
			this.allConditions = new ArrayList<IHttpCondition>();
			updateAllConditions();
		}
	}

	private void updateAllConditions() {
		synchronized (lock) {
			if (allConditions == null) {
				allConditions = new ArrayList<IHttpCondition>();
			}
			else {
				allConditions.clear();
			}
			allConditions.addAll(conditionList);
			allConditions.addAll(temporaryConditions);
		}
	}
	
	private List<IHttpCondition> getAllConditionsList() {
		if(allConditions == null) {
			allConditions = new ArrayList<IHttpCondition>();
			updateAllConditions();
		}
		return allConditions;
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public boolean matchesAll(IRequestLogRecord record) {
		activate(ActivationPurpose.READ);
		return matchesAllConditions(record);
	}

	@Override
	public boolean matchesAll(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		return matchesAllConditions(request, response);
	}

	@Override
	public boolean matchesAny(IRequestLogRecord record) {
		activate(ActivationPurpose.READ);
		return matchesAnyCondition(record);
	}

	@Override
	public boolean matchesAny(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		return matchesAnyCondition(request, response);
	}
	
	private boolean matchesAllConditions(IRequestLogRecord record) {
		synchronized(lock) {
			if(getAllConditionsList().size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: getAllConditionsList()) {
				if(c.isEnabled() && !c.matches(record)) {
					return false;
				}
			}
			return true;
		}
	}
	
	private boolean matchesAllConditions(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		synchronized(lock) {
			if (getAllConditionsList().size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: getAllConditionsList()) {
				if(c.isEnabled() && !c.matches(request, response))
					return false;
			}
			return true;
		}
	}
	
	private boolean matchesAnyCondition(IRequestLogRecord record) {
		synchronized (lock) {
			if(getAllConditionsList().size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: getAllConditionsList()) {
				if(c.isEnabled() && c.matches(record)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean matchesAnyCondition(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		synchronized(lock) {
			if (getAllConditionsList().size() == 0) {
				return matchOnEmptySet;
			}
			for(IHttpCondition c: getAllConditionsList()) {
				if(c.isEnabled() && c.matches(request, response)) {
					return true;
				}
			}
			return false;
		}
		
	}

	@Override
	public void notifyChanged() {
		conditionManager.notifyConditionSetChanged(this);
	}

	@Override
	public boolean hasActiveConditions(boolean includeInternal) {
		return !(getAllTemporaryConditions(includeInternal).isEmpty() && getAllConditions(includeInternal).isEmpty());
	}

	@Override
	public void appendCondition(IHttpCondition condition, boolean notify) {
		activate(ActivationPurpose.READ);
		conditionList.add(condition);
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public void removeCondition(IHttpCondition condition, boolean notify) {
		activate(ActivationPurpose.READ);
		conditionList.remove(condition);
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public void clearConditions(boolean notify) {
		activate(ActivationPurpose.READ);
		conditionList.clear();
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public List<IHttpCondition> getAllConditions() {
		return getAllConditions(false);
	}
	
	@Override
	public List<IHttpCondition> getAllConditions(boolean includeInternal) {
		activate(ActivationPurpose.READ);
		if(!includeInternal) {
			return Collections.unmodifiableList(new ArrayList<IHttpCondition>(conditionList));
		}
		final List<IHttpCondition> result = new ArrayList<IHttpCondition>();
		for(IHttpCondition condition: conditionList) {
			if(!condition.isInternal()) {
				result.add(condition);
			}
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public void appendTemporaryCondition(IHttpCondition condition, boolean notify) {
		temporaryConditions.add(condition);
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public void removeTemporaryCondition(IHttpCondition condition, boolean notify) {
		temporaryConditions.remove(condition);
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public void clearTemporaryConditions(boolean notify) {
		temporaryConditions.clear();
		updateAllConditions();
		if(notify) {
			conditionManager.notifyConditionSetChanged(this);
		}
	}

	@Override
	public List<IHttpCondition> getAllTemporaryConditions(boolean includeInternal) {
		if(!includeInternal) {
			return Collections.unmodifiableList(new ArrayList<IHttpCondition>(temporaryConditions));
		}
		final List<IHttpCondition> result = new ArrayList<IHttpCondition>();
		for(IHttpCondition condition: temporaryConditions) {
			if(!condition.isInternal()) {
				result.add(condition);
			}
		}
		return Collections.unmodifiableList(result);
	}

	@Override
	public IHttpConditionManager getConditionManager() {
		return conditionManager;
	}

	void setConditionManager(HttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
		this.temporaryConditions = new ArrayList<IHttpCondition>();
		this.lock = new Object();
	}
	
	public List<IRequestLogRecord> filterRequestLog(ObjectContainer db) {
		activate(ActivationPurpose.READ);
		if(!hasRecords(db)) {
			return Collections.emptyList();
		} else {
			return executeFilterQuery(db);
		}
	}

	public ObjectSet<IRequestLogRecord> executeFilterQuery(ObjectContainer db) {
		activate(ActivationPurpose.READ);
		final Query query = db.query();
		query.constrain(IRequestLogRecord.class);
		
		Constraint orChain = null;
		Constraint andChain = null;
		
		for(IHttpCondition c: getAllConditionsList()) {
			Constraint result = ((AbstractCondition)c).filterRequestLogQuery(query);
			if(c.isSufficient()) {
				orChain = processConstraintChain(result, orChain, false);
			} else {
				andChain = processConstraintChain(result, andChain, true);
			}
		}
		if(orChain != null && andChain != null) {
			orChain.or(andChain);
		}
	
		// If there are only 'sufficient' conditions, then don't filter at all
		if(andChain == null) {
			final Query q = db.query();
			q.constrain(IRequestLogRecord.class);
			return q.execute();
		}
	
		return query.execute();
	}

	private Constraint processConstraintChain(Constraint newConstraint, Constraint chain, boolean useAnd) {
		if(newConstraint == null) {
			return chain;
		} else if(chain == null) {
			return newConstraint;
		} else if(useAnd) {
			return chain.and(newConstraint);
		} else {
			return chain.or(newConstraint);
		}
	}

	private boolean hasRecords(ObjectContainer db) {
		final Query query = db.query();
		query.constrain(IRequestLogRecord.class);
		return query.execute().hasNext();
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
