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

import com.subgraph.vega.api.model.conditions.match.IHttpConditionIntegerMatchAction;

public abstract class IntegerMatchAction implements IHttpConditionIntegerMatchAction, IHttpConditionMatchActionEx {
	
	protected int integerValue;
	
	IntegerMatchAction() { 
	}
	
	protected IntegerMatchAction(int value) {
		integerValue = value;
	}
	
	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_INTEGER;
	}
	
	@Override
	public void setInteger(int value) {
		integerValue = value;
	}

	@Override
	public String getArgumentAsString() {
		return Integer.toString(integerValue);
	}
	
	@Override
	public boolean setArgumentFromString(String value) {
		final Integer n = stringToInteger(value);
		if(n == null)
			return false;
		integerValue = n;
		return true;
	}
	
	@Override
	public boolean isValidArgumentString(String value) {
		return stringToInteger(value) != null;
	}
	
	private Integer stringToInteger(String s) {
		try {
			int n = Integer.parseInt(s.trim());
			return (n < 0) ? (null) : (n);			
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
