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

import java.util.List;

import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.IMatchActionSet;

public abstract class ConditionType implements IHttpConditionType {

	private final String name;
	private final boolean isInternal;
	private final IMatchActionSet matchActionSet;
	
	ConditionType(String name, IMatchActionSet matchActionSet) {
		this(name, matchActionSet, false);
	}

	ConditionType(String name, IMatchActionSet matchActionSet, boolean isInternal) {
		this.name = name;
		this.isInternal = isInternal;
		this.matchActionSet = matchActionSet;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isInternal() {
		return isInternal;
	}
	
	@Override
	public List<IHttpConditionMatchAction> getMatchActions() {
		return matchActionSet.createMatchActions();
	}

	@Override
	public IHttpConditionMatchAction getMatchActionByName(String name) {
		for(IHttpConditionMatchAction action: getMatchActions()) {
			if(action.getLabel().equals(name)) {
				return action;
			}
		}
		return null;
	}

	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof ConditionType) {
			ConditionType that = (ConditionType) other;
			return this.name.equals(that.name);
		}
		return false;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
}
