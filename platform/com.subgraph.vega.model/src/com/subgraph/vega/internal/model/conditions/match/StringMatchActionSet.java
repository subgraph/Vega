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

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class StringMatchActionSet implements IMatchActionSet {

	@Override
	public List<IHttpConditionMatchAction> createMatchActions() {
		final List<IHttpConditionMatchAction> actions = new ArrayList<IHttpConditionMatchAction>();
		actions.add(new ContainsAction());
		actions.add(new DoesNotContainAction());
		actions.add(new MatchesRegexAction());
		actions.add(new DoesNotMatchRegexAction());
		actions.add(new StartsWithAction());
		actions.add(new DoesNotStartWithAction());
		actions.add(new EndsWithAction());
		actions.add(new DoesNotEndWithAction());
		return actions;
	}
}
