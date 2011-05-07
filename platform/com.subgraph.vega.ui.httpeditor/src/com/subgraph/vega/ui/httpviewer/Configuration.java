package com.subgraph.vega.ui.httpviewer;

import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.subgraph.vega.ui.httpviewer.syntax.HttpHeaderScanner;
import com.subgraph.vega.ui.httpviewer.syntax.RequestLineScanner;
import com.subgraph.vega.ui.httpviewer.syntax.ResponseLineScanner;

public class Configuration extends SourceViewerConfiguration {

	private final Colors colors;
	private final IPresentationReconciler reconciler;

	Configuration(Colors colors) {
		this.colors = colors;
		this.reconciler = createReconciler();
	}

	private IPresentationReconciler createReconciler() {
		final PresentationReconciler pr = new PresentationReconciler();
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_REQUEST_LINE, new RequestLineScanner(colors));
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_RESPONSE_LINE, new ResponseLineScanner(colors));
		addDamagerRepairer(pr, HttpMessageDocumentFactory.PARTITION_MESSAGE_HEADER, new HttpHeaderScanner(colors));
		return pr;
	}
	private void addDamagerRepairer(PresentationReconciler pr, String partition, ITokenScanner scanner) {
		final DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		pr.setDamager(dr, partition);
		pr.setRepairer(dr, partition);
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		return reconciler;
	}
	
}
