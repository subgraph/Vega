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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.db4o.query.Constraint;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionStringMatchAction;

public abstract class RegexMatchAction implements IHttpConditionStringMatchAction, IHttpConditionMatchActionEx {

	private String patternString;
	
	private transient Pattern pattern;
	private transient boolean patternCompileFailed;
	private transient Evaluation regexConstraint;
	
	protected RegexMatchAction() {		
	}

	protected RegexMatchAction(String value) {
		patternString = value;
	}

	protected String getPatternString() {
		return patternString;
	}

	@Override
	public MatchActionArgumentType getArgumentType() {
		return MatchActionArgumentType.ARGUMENT_REGEX;
	}

	@Override
	public String getArgumentAsString() {
		return patternString;
	}

	@Override
	public boolean setArgumentFromString(String value) {
		if(!isValidArgumentString(value))
			return false;
	
		setString(value);
		return true;
	}

	@Override
	public boolean isValidArgumentString(String value) {
		return createRegexPattern(value) != null;
	}

	protected boolean matchesRegex(String value) {
		final Pattern regex = getRegexPattern();
		if(regex == null || value == null) {
			return false;
		}
		return regex.matcher(value).find();
	}

	@Override
	public Constraint constrainQuery(Query query) {
		final Pattern p = getRegexPattern();
		if(p == null) {
			return null;
		}
		return query.constrain(getRegexConstraint(p));
	}
	
	protected abstract Evaluation createQueryEvaluation(Pattern pattern);
	
	private Evaluation getRegexConstraint(Pattern pattern) {
		synchronized(this) {
			if(regexConstraint == null) {
				regexConstraint = createQueryEvaluation(pattern);
			}
			return regexConstraint;
		}
	}
	
	private Pattern getRegexPattern() {
		if(patternCompileFailed)
			return null;

		synchronized(this) {
			if(pattern == null) {
				if((pattern = createRegexPattern(patternString)) == null)
					patternCompileFailed = true;
			}
			return pattern;
		}
	}
	
	private Pattern createRegexPattern(String str) {
		try {
			return Pattern.compile(str);
		} catch (PatternSyntaxException e) {
			return null;
		}
	}

	@Override
	public void setString(String value) {
		this.patternString = value;
		this.pattern = null;
		this.patternCompileFailed = false;
		this.regexConstraint = null;
	}
}
