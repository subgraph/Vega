package com.subgraph.vega.internal.model.conditions;

import java.util.regex.Pattern;

import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;

public class RegexConstrain implements Evaluation {

	private static final long serialVersionUID = 1L;

	private final Pattern pattern;
	
	RegexConstrain(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public void evaluate(Candidate candidate) {
		final String value = (String) candidate.getObject();
		candidate.include(pattern.matcher(value).matches());		
	}

}
