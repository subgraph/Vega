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
import com.subgraph.vega.api.model.conditions.match.IHttpConditionRangeMatchAction;

public class IsInsideRangeAction implements IHttpConditionRangeMatchAction, IHttpConditionMatchActionEx {

	private int rangeLowValue;
	private int rangeHighValue;

	IsInsideRangeAction() {}
	
	private static class Range {
		final int low;
		final int high;
		Range(int low, int high) {
			this.low = low;
			this.high = high;
		}
	}
	
	private IsInsideRangeAction(int low, int high) {
		this.rangeLowValue = low;
		this.rangeHighValue = high;
	}

	@Override
	public String getLabel() {
		return "is inside range";
	}
	@Override
	public void setRange(int rangeLow, int rangeHigh) {
		this.rangeLowValue = rangeLow;
		this.rangeHighValue = rangeHigh;
	}

	@Override
	public boolean matchesValue(int value) {
		return value >= rangeLowValue && value < rangeHighValue;
	}

	@Override
	public Constraint constrainQuery(Query query) {
		return query.constrain(rangeLowValue).greater().or(query.constrain(rangeLowValue).equal()).and(query.constrain(rangeHighValue).smaller());
	}
	
	@Override
	public IHttpConditionMatchAction createCopy() {
		return new IsInsideRangeAction(rangeLowValue, rangeHighValue);
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_RANGE;
	}

	@Override
	public String getArgumentAsString() {
		return rangeLowValue + " - "+ rangeHighValue;
	}

	@Override
	public boolean setArgumentFromString(String value) {
		final Range range = stringToRange(value);
		if(range == null)
			return false;
		rangeLowValue = range.low;
		rangeHighValue = range.high;
		return true;
	}

	@Override
	public boolean isValidArgumentString(String value) {
		return stringToRange(value) != null;
	}
	
	private Range stringToRange(String s) {
		String[] parts = s.split("-");
		if(parts.length != 2)
			return null;
		Integer low = stringToInteger(parts[0]);
		Integer high = stringToInteger(parts[1]);
		if(low == null || high == null || low > high)
			return null;
		
		return new Range(low, high);
		
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
