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

public class DoesNotMatchRegexAction extends RegexMatchAction {
	
	private DoesNotMatchRegexAction(String value) {
		super(value);
	}

	DoesNotMatchRegexAction() {}

	@Override
	public String getLabel() {
		return "does not match regex";
	}

	@Override
	public boolean matchesValue(String value) {
		return !matchesRegex(value);
	}

	
	@Override
	public IHttpConditionMatchAction createCopy() {
		return new DoesNotMatchRegexAction(getPatternString());
	}

	@Override
	protected Evaluation createQueryEvaluation(Pattern pattern) {
		return new RegexQueryEvaluation(pattern, true);
	}
}
