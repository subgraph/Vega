package com.subgraph.vega.ui.httpviewer;


import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpviewer.entity.HttpEntityViewer;

public class HttpMessageViewer extends Composite {
	private final Colors colors;
	private final SourceViewer viewer;
	private final HttpMessageDocumentFactory documentFactory;
	private final HttpEntityViewer entityViewer;
	private final EmbeddedControlPainter painter;
	
	private IDocument rawDocument;
	private IDocument decodedDocument;
	private boolean isDecodingEnabled;
	
	public HttpMessageViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		viewer = createSourceViewer();
		viewer.getTextWidget().setWrapIndent(20);
		entityViewer = new HttpEntityViewer(viewer.getTextWidget());
		painter = new EmbeddedControlPainter(viewer, entityViewer, 200);
		viewer.addPainter(painter);
		colors = new Colors(getDisplay());
		viewer.configure(new Configuration(colors));
		documentFactory = new HttpMessageDocumentFactory();
	}

	public void dispose() {
		colors.dispose();
		super.dispose();
	}

	public void setEditable(boolean flag) {
		viewer.setEditable(flag);
	}

	public void clearContent() {
		
	}

	public String getContent() {
		return EmbeddedControlPainter.getDocumentContent(viewer.getDocument());
	}
	
	public HttpEntity getEntityContent() {
		return entityViewer.getEntityContent();
	}

	public void setDecodeUrlEncoding(boolean flag) {
		if(flag == isDecodingEnabled)
			return;
	
		isDecodingEnabled = flag;
		displayDocumentForDecodeState();
	}
	
	private void displayDocumentForDecodeState() {
		if(isDecodingEnabled) {
			viewer.setDocument(decodedDocument);
		} else {
			viewer.setDocument(rawDocument);
		}
		viewer.refresh();
	}

	public void setDisplayImages(boolean flag) {
		entityViewer.setDisplayImages(flag);
	}
	
	public void setDisplayImagesAsHex(boolean flag) {
		entityViewer.setDisplayImagesAsHex(flag);
	}
	
	public void displayHttpRequest(HttpRequest request) {
		rawDocument = documentFactory.createDocumentForRequest(request, false);
		decodedDocument = documentFactory.createDocumentForRequest(request, true);
		displayDocumentForDecodeState();
		entityViewer.displayHttpEntity(maybeGetRequestEntity(request));
	}

	public void displayHttpRequest(IHttpRequestBuilder builder) {
		rawDocument = documentFactory.createDocumentForRequest(builder, false);
		decodedDocument = documentFactory.createDocumentForRequest(builder, true);
		displayDocumentForDecodeState();
		entityViewer.displayHttpEntity(builder.getEntity());
	}
	
	private HttpEntity maybeGetRequestEntity(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest) {
			return ((HttpEntityEnclosingRequest) request).getEntity();
		} else {
			return null;
		}
	}

	public void displayHttpResponse(HttpResponse response) {
		rawDocument = documentFactory.createDocumentForResponse(response, false);
		decodedDocument = documentFactory.createDocumentForResponse(response, true);
		displayDocumentForDecodeState();
		entityViewer.displayHttpEntity(response.getEntity());
	}
	
	public void displayHttpResponse(IHttpResponseBuilder builder) {
		rawDocument = documentFactory.createDocumentForResponse(builder, false);
		decodedDocument = documentFactory.createDocumentForResponse(builder, true);
		displayDocumentForDecodeState();
		entityViewer.displayHttpEntity(builder.getEntity());
	}

	private SourceViewer createSourceViewer() {
		final SourceViewer sv = new SourceViewer(this, new VerticalRuler(0), SWT.BORDER | SWT.MULTI | SWT.WRAP);
		return sv;
	}	
}
