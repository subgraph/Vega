package com.subgraph.vega.ui.text.httpeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.ui.httpeditor.text.highlight.ColorManager;
import com.subgraph.vega.ui.httpeditor.text.highlight.HttpHeaderScanner;
import com.subgraph.vega.ui.httpeditor.text.highlight.IHttpColorConstants;
import com.subgraph.vega.ui.httpeditor.text.hover.HoverInformationControl;
import com.subgraph.vega.ui.httpeditor.text.hover.HttpRequestHover;
import com.subgraph.vega.ui.httpeditor.text.hover.HttpRequestImageHover;
import com.subgraph.vega.ui.httpeditor.text.scanners.PartitionScanner;

public class HttpRequestSourceViewerConfiguration extends SourceViewerConfiguration {
	private final HttpRequestViewer requestViewer;
	private final ColorManager colorManager;
	
	HttpRequestSourceViewerConfiguration(HttpRequestViewer requestViewer) {
		this.requestViewer = requestViewer;
		this.colorManager = new ColorManager();
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer viewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(viewer));
		
		HttpHeaderScanner scanner = new HttpHeaderScanner(colorManager);
		
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, PartitionScanner.HTTP_HEADERS);
		reconciler.setRepairer(dr, PartitionScanner.HTTP_HEADERS);
		
		RuleBasedScanner bodyScanner = new RuleBasedScanner();
		IToken token = new Token(new TextAttribute(colorManager.getColor(IHttpColorConstants.IMAGE_TAG)));
		bodyScanner.setDefaultReturnToken(token);
		dr = new DefaultDamagerRepairer(bodyScanner);
		reconciler.setDamager(dr, PartitionScanner.HTTP_BODY);
		reconciler.setRepairer(dr, PartitionScanner.HTTP_BODY);
		
		return reconciler;
	}
	
	public String getConfiguredDocumentPartitioning(ISourceViewer viewer) {
		return HttpRequestViewer.HTTP_PARTITIONING;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer viewer) {
		return new String[] { 
				IDocument.DEFAULT_CONTENT_TYPE, 
				PartitionScanner.HTTP_HEADERS,
				PartitionScanner.HTTP_BODY
		};
	}
	
	public ITextHover getTextHover(ISourceViewer viewer, String contentType) {
		if(contentType.equals(PartitionScanner.HTTP_BODY))
			return new HttpRequestImageHover(requestViewer);
		else if(contentType.equals(PartitionScanner.HTTP_HEADERS))
			return new HttpRequestHover(requestViewer);
		else
			return null;
	}
	
	public IInformationControlCreator getInformationControlCreator(ISourceViewer viewer) {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new HoverInformationControl(parent, true);
			}
			
		};
	}
	
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		final IReconcilingStrategy strategy = new HttpRequestReconcileStrategy(sourceViewer, requestViewer);
		return new MonoReconciler(strategy, false);
	}
	
}