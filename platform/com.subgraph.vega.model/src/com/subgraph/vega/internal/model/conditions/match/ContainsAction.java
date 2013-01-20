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

public class ContainsAction extends StringMatchAction {

	private ContainsAction(String value) {
		super(value);
	}

	public ContainsAction() {
	}

	@Override
	public String getLabel() {
		return "contains";
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(stringValue).contains();
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new ContainsAction(stringValue);
	}

	@Override
	public boolean matchesValue(String value) {
		return matchesContains(value);
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}
}
