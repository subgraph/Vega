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

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.query.Query;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionIntegerMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionRangeMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionStringMatchAction;
import com.subgraph.vega.internal.model.conditions.match.IHttpConditionMatchActionEx;

public abstract class AbstractCondition implements IHttpCondition, Activatable {
		
	protected IHttpConditionMatchActionEx matchAction;
	private boolean isEnabled;
		
	protected AbstractCondition(IHttpConditionMatchAction matchAction) {
		this.matchAction = (IHttpConditionMatchActionEx) matchAction;
		this.isEnabled = true;
	}

	@Override
	public IHttpConditionMatchAction getMatchAction() {
		activate(ActivationPurpose.READ);
		return matchAction;
	}
	
	@Override
	public void setMatchAction(IHttpConditionMatchAction matchAction) {
		activate(ActivationPurpose.READ);
		if(matchAction instanceof IHttpConditionMatchActionEx) {
			this.matchAction = (IHttpConditionMatchActionEx) matchAction;
			activate(ActivationPurpose.WRITE);
		}
	}

	@Override
	public boolean isEnabled() {
		activate(ActivationPurpose.READ);
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean state) {
		activate(ActivationPurpose.READ);
		isEnabled = state;
		activate(ActivationPurpose.WRITE);
	}

	public IHttpCondition createCopy() {
		activate(ActivationPurpose.READ);
		return getType().createConditionInstance(matchAction.createCopy());
	}

	protected boolean matchesString(String value) {
		activate(ActivationPurpose.READ);
		if(matchAction instanceof IHttpConditionStringMatchAction) {
			return ((IHttpConditionStringMatchAction) matchAction).matchesValue(value);
		}
		throw new IllegalStateException("Expecting an IHttpConditionStringMatchingAction, got"+ matchAction);
	}
	
	protected boolean matchesInteger(int value) {
		activate(ActivationPurpose.READ);
		if(matchAction instanceof IHttpConditionIntegerMatchAction) {
			return ((IHttpConditionIntegerMatchAction) matchAction).matchesValue(value);
		} else if(matchAction instanceof IHttpConditionRangeMatchAction) {
			return ((IHttpConditionRangeMatchAction) matchAction).matchesValue(value);
		} 
		throw new IllegalStateException("Expecting an IHttpConditonIntegerMatchAction or IHttpConditionRangeMatchAction, got"+ matchAction);
	}
	
	protected void constrainQuery(Query query) {
		activate(ActivationPurpose.READ);
		matchAction.constrainQuery(query);
	}

	public String getValueString() {
		activate(ActivationPurpose.READ);
		return matchAction.toString();
	}
	
	public abstract void filterRequestLogQuery(Query query);
		
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
