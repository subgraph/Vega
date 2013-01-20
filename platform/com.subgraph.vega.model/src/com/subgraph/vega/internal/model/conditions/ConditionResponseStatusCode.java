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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.IntegerMatchActionSet;

public class ConditionResponseStatusCode extends AbstractCondition {
	

	static private transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized(ConditionResponseStatusCode.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}

	private static IHttpConditionType createType() {
		return new ConditionType("response status code", new IntegerMatchActionSet()) {			
			@Override
			public IHttpCondition createConditionInstance(IHttpConditionMatchAction matchAction) {
				return new ConditionResponseStatusCode(matchAction);
			}
		};
	}
	
	private ConditionResponseStatusCode(IHttpConditionMatchAction matchAction) {
		super(matchAction);
	}

	@Override
	public boolean matches(HttpRequest request) {
		return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		if(response == null)
			return false;
		return matchesInteger(response.getStatusLine().getStatusCode());
	}

	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		return matches(response);
	}

	@Override
	public IHttpConditionType getType() {
		return getConditionType();
	}

	@Override
	public Constraint filterRequestLogQuery(Query query) {
		return constrainQuery(query.descend("responseCode"));		
	}
}
