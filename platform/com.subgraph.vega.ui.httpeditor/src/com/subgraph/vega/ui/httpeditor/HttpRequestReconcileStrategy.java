package com.subgraph.vega.ui.httpeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import com.subgraph.vega.ui.httpeditor.dom.RequestModel;
import com.subgraph.vega.ui.httpeditor.dom.RequestParser;

public class HttpRequestReconcileStrategy implements IReconcilingStrategy {

	@SuppressWarnings("unused")
	private final ISourceViewer sourceViewer;
	private final HttpRequestViewer requestViewer;
	private final RequestParser parser;
	
	private IDocument document;
	
	HttpRequestReconcileStrategy(ISourceViewer sourceViewer, HttpRequestViewer requestViewer) {
		this.sourceViewer = sourceViewer;
		this.requestViewer = requestViewer;
		this.parser = new RequestParser();
	}
	@Override
	public void reconcile(IRegion partition) {
		final RequestModel model = parser.parse(document, requestViewer.getAnnotationModel());
		if(model != null) {
			//model.printModel();
			requestViewer.setRequestModel(model);
		}
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		throw new IllegalStateException();		
	}

	@Override
	public void setDocument(IDocument document) {
		this.document = document;		
	}

}