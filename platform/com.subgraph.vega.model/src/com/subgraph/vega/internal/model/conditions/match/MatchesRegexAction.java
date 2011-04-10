package com.subgraph.vega.internal.model.conditions.match;

import java.util.regex.Pattern;

import com.db4o.query.Evaluation;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;

public class MatchesRegexAction extends RegexMatchAction {
	
	private MatchesRegexAction(String value) {
		super(value);
	}

	MatchesRegexAction() {
	}

	@Override
	public String getLabel() {
		return "matches regex";
	}

	@Override
	public IHttpConditionMatchAction createCopy() {
		return new MatchesRegexAction(getPatternString());
	}

	@Override
	public boolean matchesValue(String value) {
		return matchesRegex(value);
	}

	@Override
	protected Evaluation createQueryEvaluation(Pattern pattern) {
		return new RegexQueryEvaluation(pattern, false);
	}
}
