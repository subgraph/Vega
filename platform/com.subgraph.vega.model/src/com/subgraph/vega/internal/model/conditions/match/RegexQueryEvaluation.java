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

import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;

public class RegexQueryEvaluation implements Evaluation {

	private static final long serialVersionUID = 1L;

	private final Pattern pattern;
	private final boolean invertMatch;
	
	public RegexQueryEvaluation(Pattern pattern, boolean invertMatch) {
		this.pattern = pattern;
		this.invertMatch = invertMatch;
	}

	@Override
	public void evaluate(Candidate candidate) {
		final String value = (String) candidate.getObject();
		final boolean result = pattern.matcher(value).find();
		candidate.include(result ^ invertMatch);
	}
}
