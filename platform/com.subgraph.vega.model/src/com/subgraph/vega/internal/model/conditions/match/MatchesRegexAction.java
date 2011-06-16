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
