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
package com.subgraph.vega.ui.httpviewer.entity;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

import com.subgraph.vega.ui.httpviewer.Colors;
import com.subgraph.vega.ui.httpviewer.html.HtmlPartitionScanner;
import com.subgraph.vega.ui.httpviewer.html.TagScanner;

public class HtmlTextViewerConfiguration extends SourceViewerConfiguration {
	
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}
	
	private final Colors colors;
	
	public HtmlTextViewerConfiguration(Colors colors) {
		this.colors = colors;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		final PresentationReconciler pr = new PresentationReconciler();
	
		addDamagerRepairer(pr, new TagScanner(colors), HtmlPartitionScanner.HTML_START_TAG);
		addDamagerRepairer(pr, new TagScanner(colors), HtmlPartitionScanner.HTML_END_TAG);
		addDamagerRepairer(pr, Colors.MULTI_LINE_COMMENT, HtmlPartitionScanner.HTML_COMMENT);
		addDamagerRepairer(pr, Colors.MULTI_LINE_COMMENT, HtmlPartitionScanner.HTML_DOCTYPE);
		addDamagerRepairer(pr, Colors.OTHER, IDocument.DEFAULT_CONTENT_TYPE);
		
		return pr;
	}
	
	private void addDamagerRepairer(PresentationReconciler pr, RGB color, String type) {
		final TextAttribute ta = new TextAttribute(colors.get(color));
		addDamagerRepairer(pr, new SingleTokenScanner(ta), type);
	}
	
	private void addDamagerRepairer(PresentationReconciler pr, ITokenScanner scanner, String type) {
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		pr.setDamager(dr, type);
		pr.setRepairer(dr, type);
	}
}
