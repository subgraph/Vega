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

import java.util.List;

import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.IPredicateRule;

public interface ITextEntityRenderer {
	List<String> getPartitionTypes();
	boolean matchContentType(String contentType);
	void addPartitionScannerRules(List<IPredicateRule> rules);
	String getDefaultPartitionType();
	void configurePresentationReconciler(PresentationReconciler reconciler, Colors colors);
	String formatText(String input);
	String getLineSplitChars();
}
