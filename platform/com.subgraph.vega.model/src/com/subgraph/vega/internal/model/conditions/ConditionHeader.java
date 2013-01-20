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

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.activation.ActivationPurpose;
import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.internal.model.conditions.match.StringMatchActionSet;

public class ConditionHeader extends AbstractCondition {
	static private transient IHttpConditionType requestConditionType;
	static private transient IHttpConditionType responseConditionType;
	
	static IHttpConditionType getRequestConditionType() {
		synchronized (ConditionHeader.class) {
			if(requestConditionType == null) 
				createTypes();
			return requestConditionType;
		}
	}
	
	static IHttpConditionType getResponseConditionType() {
		synchronized(ConditionHeader.class) {
			if(responseConditionType == null)
				createTypes();
			return responseConditionType;
		}
	}

	private static void createTypes() {
		requestConditionType = createType("request header", true);
		responseConditionType = createType("response header", false);
	}
	
	private static IHttpConditionType createType(String label, final boolean flag) {
		return new ConditionType(label, new StringMatchActionSet()) {			
			@Override
			public IHttpCondition createConditionInstance(IHttpConditionMatchAction matchAction) {
				return new ConditionHeader(flag, matchAction);
			}
		};
	}

	private final boolean matchRequestHeader;
	
	ConditionHeader(boolean matchRequestHeader, IHttpConditionMatchAction matchAction) {
		super(matchAction);
		this.matchRequestHeader = matchRequestHeader;
	}
	
	@Override
	public boolean matches(HttpRequest request) {
		activate(ActivationPurpose.READ);
		if(matchRequestHeader)
			return matchesString(headersToString(request));
		else
			return false;
	}

	@Override
	public boolean matches(HttpResponse response) {
		activate(ActivationPurpose.READ);
		if(!matchRequestHeader)
			return matchesString(headersToString(response));
		else
			return false;
	}

	
	private String headersToString(HttpMessage message) {
		if(message == null)
			return "";
		final StringBuilder sb = new StringBuilder();
		for(Header h: message.getAllHeaders()) {
			sb.append(h.getName());
			sb.append(": ");
			sb.append(h.getValue());
			sb.append("\r\n");
		}
		return sb.toString();
	}
	
	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		activate(ActivationPurpose.READ);
		if(matchRequestHeader)
			return matchesString(headersToString(request));
		else
			return matchesString(headersToString(response));
	}

	@Override
	public IHttpConditionType getType() {
		activate(ActivationPurpose.READ);
		if(matchRequestHeader)
			return getRequestConditionType();
		else
			return getResponseConditionType();
	}

	@Override
	public Constraint filterRequestLogQuery(Query query) {
		activate(ActivationPurpose.READ);
		if(matchRequestHeader) 
			return constrainQuery(query.descend("requestHeaders"));
		else
			return constrainQuery(query.descend("responseHeaders"));		
	}
}
