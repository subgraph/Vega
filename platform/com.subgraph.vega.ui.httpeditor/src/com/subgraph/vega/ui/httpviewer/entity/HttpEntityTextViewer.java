package com.subgraph.vega.ui.httpviewer.entity;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.ui.httpviewer.Colors;

public class HttpEntityTextViewer extends Composite {
	private final TextEntityDocumentFactory documentFactory;
	private final JavascriptViewerConfiguration javascriptConfiguration;
	private final HtmlTextViewerConfiguration htmlConfiguration;
	private final SourceViewer viewer;
	private final Colors colors;
	
	private IDocument currentDocument;
	private boolean isDirty;
	
	HttpEntityTextViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		this.viewer = createSourceViewer();
		this.colors = new Colors(getDisplay());
		this.documentFactory = new TextEntityDocumentFactory();
		this.javascriptConfiguration = new JavascriptViewerConfiguration(colors);
		this.htmlConfiguration = new HtmlTextViewerConfiguration(colors);
	}
	
	private SourceViewer createSourceViewer() {
		return new SourceViewer(this, new VerticalRuler(0), SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
	}

	public void setInput(String text, String contentType) {
		viewer.unconfigure();
		currentDocument = documentFactory.createDocument(text, contentType);
		viewer.setDocument(currentDocument);
		configureForContentType(contentType);
		viewer.refresh();
		isDirty = false;
		currentDocument.addDocumentListener(new IDocumentListener() {
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
			@Override
			public void documentChanged(DocumentEvent event) {
				isDirty = true;
			}
		});
	}

	public void clear() {
		viewer.setInput(null);
	}
	
	public boolean isContentDirty() {
		return isDirty;
	}
	
	public HttpEntity getEntityContent() {
		if(currentDocument == null) {
			return null;
		} else {
			return createEntityForDocument(currentDocument);
		}
	}

	private HttpEntity createEntityForDocument(IDocument document) {
		try {
			return new StringEntity(document.get());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Failed to create entity from document", e);
		}
	}
	
	private void configureForContentType(String contentType) {
		final String lower = contentType.toLowerCase();
		if(isJavascript(lower)) {
			viewer.configure(javascriptConfiguration);
		} else if(isHtml(lower)) {
			viewer.configure(htmlConfiguration);
		} 
	}
	private boolean isJavascript(String contentType) {
		return contentType.contains("javascript") || contentType.contains("json");
	}
	
	private boolean isHtml(String contentType) {
		return contentType.contains("html");
	}
	
	@Override
	public void dispose() {
		colors.dispose();
		super.dispose();
	}
}
