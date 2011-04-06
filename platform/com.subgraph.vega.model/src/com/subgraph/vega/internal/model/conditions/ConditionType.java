package com.subgraph.vega.internal.model.conditions;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public abstract class ConditionType implements IHttpConditionType {

	private final String name;
	private final HttpConditionStyle style;
	
	ConditionType(String name, HttpConditionStyle style) {
		this.name = name;
		this.style = style;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public HttpConditionStyle getStyle() {
		return style;
	}
	
	public abstract IHttpCondition createConditionInstance();
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof ConditionType) {
			ConditionType that = (ConditionType) other;
			return this.name.equals(that.name) && this.style.equals(that.style);
		}
		return false;
	}
	
	public int HashCode() {
		return 17 * (37 + name.hashCode()) + style.hashCode();
	}

}
