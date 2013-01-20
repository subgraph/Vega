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

import com.subgraph.vega.api.model.conditions.match.IHttpConditionStringMatchAction;

public abstract class StringMatchAction implements IHttpConditionMatchActionEx, IHttpConditionStringMatchAction {
	protected String stringValue;
	
	protected StringMatchAction() {}
	
	protected StringMatchAction(String value) {
		this.stringValue = value;
	}

	@Override
	public String getArgumentAsString() {
		return stringValue;
	}
	
	@Override
	public boolean setArgumentFromString(String value) {
		stringValue = value;
		return true;
	}

	@Override
	public boolean isValidArgumentString(String value) {
		return true;
	}

	protected boolean matchesContains(String value) {
		if(value == null) {
			return false;
		}
		return value.contains(stringValue);
	}
	
	@Override
	public void setString(String value) {
		this.stringValue = value;
	}
}
