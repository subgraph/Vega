package com.subgraph.vega.internal.model.conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.db4o.activation.ActivationPurpose;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpRegexCondition;

public abstract class AbstractRegexCondition extends AbstractCondition implements IHttpRegexCondition {
	
	private String patternString;
	private boolean isSimplePattern;
	private transient Pattern patternRegex;
	private transient Evaluation regexConstraint;
	
	@Override
	public void setPattern(String pattern) {
		activate(ActivationPurpose.WRITE);
		this.patternString = pattern;
		this.isSimplePattern = isSimplePattern(pattern);
	}

	private boolean isSimplePattern(String pattern) {
		for(int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			if(!(Character.isLetterOrDigit(c) || Character.isWhitespace(c)))
				return false;					
		}
		return true;
	}

	@Override
	public String getPattern() {
		activate(ActivationPurpose.READ);
		return patternString;
	}
	
	private Evaluation getRegexConstraint() {
		synchronized(this) {
			if(regexConstraint == null)
				regexConstraint = new RegexConstrain(getRegexPattern());
			return regexConstraint;
		}
	}
	
	protected void constrainQuery(Query query) {
		if(isSimplePattern) {
			query.constrain(patternString).contains();
		} else {
			query.constrain(getRegexConstraint());
		}
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
