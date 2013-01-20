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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public interface IHttpCondition {
	IHttpConditionType getType();
	IHttpConditionMatchAction getMatchAction();
	void setMatchAction(IHttpConditionMatchAction matchAction);
	IHttpCondition createCopy();
	String getValueString();
	boolean matches(IRequestLogRecord record);
	boolean matches(HttpRequest request);
	boolean matches(HttpResponse response);
	boolean matches(HttpRequest request, HttpResponse response);
	boolean isEnabled();
	void setEnabled(boolean state);
	boolean isInternal();
	/**
	 * Returns true if the sufficient flag has been set.  A sufficient condition will cause a {@link IHttpConditionSet#matchesAll(IRequestLogRecord)}
	 * to return true if at least one sufficient condition matches the query.
	 * @return
	 */
	boolean isSufficient();
	void setSufficient(boolean value);
}
