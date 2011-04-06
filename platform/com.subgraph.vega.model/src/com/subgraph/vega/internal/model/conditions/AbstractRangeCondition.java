package com.subgraph.vega.internal.model.conditions;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpRangeCondition;

public abstract class AbstractRangeCondition extends AbstractCondition implements IHttpRangeCondition {

	private int rangeLow;
	private int rangeHigh;

	public void setRangeLow(int low) {
		activate(ActivationPurpose.WRITE);
		rangeLow = low;
	}
	
	public void setRangeHigh(int high) {
		activate(ActivationPurpose.WRITE);
		rangeHigh = high;
	}
	
	public int getRangeLow() {
		activate(ActivationPurpose.READ);
		return rangeLow;
	}
	
	public int getRangeHigh() {
		activate(ActivationPurpose.READ);
		return rangeHigh;
	}
	
	protected boolean matchesRange(int value) {
		return value >= rangeLow && value < rangeHigh;
	}
	
	public IHttpCondition createCopy() {
		final IHttpRangeCondition c = (IHttpRangeCondition) getType().createConditionInstance();
		c.setEnabled(isEnabled());
		c.setInverted(isInverted());
		c.setRangeLow(rangeLow);
		c.setRangeHigh(rangeHigh);
		return c;
	}
}
