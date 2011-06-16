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
package com.subgraph.vega.api.model.conditions;

import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface IHttpConditionSet {
	String getName();
	boolean matchesAll(HttpRequest request, HttpResponse response);
	boolean matchesAny(HttpRequest request, HttpResponse response);
	void appendCondition(IHttpCondition condition);
	void removeCondition(IHttpCondition condition);
	void clearConditions();
	List<IHttpCondition> getAllConditions();
	IHttpConditionManager getConditionManager();
	void setMatchOnEmptySet(boolean flag);
}
