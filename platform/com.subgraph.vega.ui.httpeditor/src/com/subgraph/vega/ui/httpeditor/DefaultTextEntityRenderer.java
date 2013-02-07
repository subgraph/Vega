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
package com.subgraph.vega.ui.httpeditor;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.IPredicateRule;

public class DefaultTextEntityRenderer implements ITextEntityRenderer {

	@Override
	public List<String> getPartitionTypes() {
		return Collections.emptyList();
	}

	@Override
	public boolean matchContentType(String contentType) {
		return true;
	}

	@Override
	public void addPartitionScannerRules(List<IPredicateRule> rules) {
	}

	@Override
	public void configurePresentationReconciler(
			PresentationReconciler reconciler, Colors colors) {
	}

	@Override
	public String formatText(String input) {
		return input;
	}

	@Override
	public String getLineSplitChars() {
		return " ,";
	}

	@Override
	public String getDefaultPartitionType() {
		return null;
	}
}
