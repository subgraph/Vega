package com.subgraph.vega.internal.model.conditions;

import java.util.ArrayList;
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
		activate(ActivationPurpose.READ);
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
		activate(ActivationPurpose.READ);
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
	public void clearConditions() {
		activate(ActivationPurpose.READ);
		conditionList.clear();
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

	void setConditionManager(IHttpConditionManager conditionManager) {
		activate(ActivationPurpose.WRITE);
		this.conditionManager = conditionManager;
	}
	
	public List<IRequestLogRecord> filterRequestLog(ObjectContainer db) {
		activate(ActivationPurpose.READ);
		if(conditionList.isEmpty())
			return db.query(IRequestLogRecord.class);
		
		final Query query = db.query();
		query.constrain(IRequestLogRecord.class);
		for(IHttpCondition c: conditionList) {
			((AbstractCondition) c).filterRequestLogQuery(query);
		}
		return query.execute();		
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
