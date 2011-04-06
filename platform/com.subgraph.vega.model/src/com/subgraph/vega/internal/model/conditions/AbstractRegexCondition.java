package com.subgraph.vega.internal.model.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpRegexCondition;

public abstract class AbstractRegexCondition extends AbstractCondition implements IHttpRegexCondition {
	
	private String patternString;
	private transient Pattern patternRegex;
	
	@Override
	public void setPattern(String pattern) {
		activate(ActivationPurpose.WRITE);
		this.patternString = pattern;
	}
	
	@Override
	public String getPattern() {
		activate(ActivationPurpose.READ);
		return patternString;
	}
	
	protected Pattern getRegexPattern() {
		synchronized(this) {
			if(patternRegex == null)
				patternRegex = Pattern.compile(patternString);
			return patternRegex;
		}
	}
	
	protected boolean matchesPattern(String value) {
		final Matcher matcher = getRegexPattern().matcher(value);
		return matcher.find();
	}
	
	public IHttpCondition createCopy() {
		final IHttpRegexCondition c = (IHttpRegexCondition) getType().createConditionInstance();
		c.setEnabled(isEnabled());
		c.setInverted(isInverted());
		c.setPattern(patternString);
		return c;
	}
}
