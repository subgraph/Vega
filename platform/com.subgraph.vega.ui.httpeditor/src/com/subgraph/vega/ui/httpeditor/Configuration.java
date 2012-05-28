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

import com.subgraph.vega.ui.httpeditor.html.HtmlPartitionScanner;
import com.subgraph.vega.ui.httpeditor.html.TagScanner;
import com.subgraph.vega.ui.httpeditor.http.HttpHeaderScanner;
import com.subgraph.vega.ui.httpeditor.http.RequestLineScanner;
import com.subgraph.vega.ui.httpeditor.http.ResponseLineScanner;
import com.subgraph.vega.ui.httpeditor.js.JavascriptPartitionScanner;
import com.subgraph.vega.ui.httpeditor.js.JavascriptScanner;

public class Configuration extends SourceViewerConfiguration {

	private final Colors colors;
	private final IPresentationReconciler reconciler;
	
	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}
	Configuration(Colors colors) {
		this.colors = colors;
		this.reconciler = createReconciler();
	}

	private IPresentationReconciler createReconciler() {
		final PresentationReconciler pr = new PresentationReconciler();
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_REQUEST_LINE, new RequestLineScanner(colors));
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_RESPONSE_LINE, new ResponseLineScanner(colors));
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_MESSAGE_HEADER, new HttpHeaderScanner(colors));
		
		addDamagerRepairer(pr, JavascriptPartitionScanner.JS_DEFAULT_TYPE, new JavascriptScanner(colors));
		addDamagerRepairer(pr, Colors.MULTI_LINE_COMMENT, JavascriptPartitionScanner.JS_MULTILINE_COMMENT);

		addDamagerRepairer(pr, HtmlPartitionScanner.HTML_START_TAG, new TagScanner(colors));
		addDamagerRepairer(pr, HtmlPartitionScanner.HTML_END_TAG, new TagScanner(colors));
		addDamagerRepairer(pr, Colors.MULTI_LINE_COMMENT, HtmlPartitionScanner.HTML_COMMENT);
		addDamagerRepairer(pr, Colors.MULTI_LINE_COMMENT, HtmlPartitionScanner.HTML_DOCTYPE);
		
		addDamagerRepairer(pr, Colors.OTHER, IDocument.DEFAULT_CONTENT_TYPE);
		return pr;
	}

	private void addDamagerRepairer(PresentationReconciler pr, String partition, ITokenScanner scanner) {
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		pr.setDamager(dr, partition);
		pr.setRepairer(dr, partition);
	}
	
	private void addDamagerRepairer(PresentationReconciler pr, RGB color, String type) {
		final TextAttribute ta = new TextAttribute(colors.get(color));
		addDamagerRepairer(pr, type, new SingleTokenScanner(ta));
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		return reconciler;
	}
	
}
