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
package com.subgraph.vega.internal.model.conditions.match;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class IsLessThanAction extends IntegerMatchAction {
	
	IsLessThanAction() {
	}
	
	private IsLessThanAction(int value) {
		super(value);
	}
	
	@Override
	public String getLabel() {
		return "is less than";
	}

	@Override
	public boolean matchesValue(int value) {
		return value < integerValue;
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(integerValue).smaller();
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new IsLessThanAction(integerValue);
	}
}
