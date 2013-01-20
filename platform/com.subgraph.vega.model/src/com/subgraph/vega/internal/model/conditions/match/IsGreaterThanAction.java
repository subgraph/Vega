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

public class IsGreaterThanAction extends IntegerMatchAction {
	
	IsGreaterThanAction() {
	}
	
	private IsGreaterThanAction(int value) {
		super(value);
	}

	@Override
	public String getLabel() {
		return "is greater than";
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(integerValue).greater();		
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new IsGreaterThanAction(integerValue);
	}

	@Override
	public boolean matchesValue(int value) {
		return value > integerValue;
	}
}
