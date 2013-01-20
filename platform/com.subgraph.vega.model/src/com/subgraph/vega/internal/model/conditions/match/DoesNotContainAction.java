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

public class DoesNotContainAction extends StringMatchAction {
	
	private DoesNotContainAction(String value) {
		super(value);
	}

	public DoesNotContainAction() {
	}

	@Override
	public String getLabel() {
		return "does not contain";
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new DoesNotContainAction(stringValue);
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(stringValue).contains().not();
	}

	@Override
	public boolean matchesValue(String value) {
		return !matchesContains(value);
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_STRING;
	}	
}
