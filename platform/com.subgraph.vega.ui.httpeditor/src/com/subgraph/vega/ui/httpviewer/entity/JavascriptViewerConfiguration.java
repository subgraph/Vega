package com.subgraph.vega.ui.httpviewer.entity;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.subgraph.vega.ui.httpviewer.Colors;
import com.subgraph.vega.ui.httpviewer.syntax.js.JavascriptPartitionScanner;
import com.subgraph.vega.ui.httpviewer.syntax.js.JavascriptScanner;

public class JavascriptViewerConfiguration extends SourceViewerConfiguration {

	static class SingleTokenScanner extends BufferedRuleBasedScanner {
		public SingleTokenScanner(TextAttribute attribute) {
			setDefaultReturnToken(new Token(attribute));
		}
	}
	
	private final Colors colors;
	
	JavascriptViewerConfiguration(Colors colors) {
		this.colors = colors;
	}


	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		final PresentationReconciler pr = new PresentationReconciler();
		
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new JavascriptScanner(colors));
		pr.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		pr.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(new TextAttribute(colors.get(Colors.MULTI_LINE_COMMENT))));
		pr.setDamager(dr, JavascriptPartitionScanner.JS_MULTILINE_COMMENT);
		pr.setRepairer(dr, JavascriptPartitionScanner.JS_MULTILINE_COMMENT);
		return pr;
	}	
}
