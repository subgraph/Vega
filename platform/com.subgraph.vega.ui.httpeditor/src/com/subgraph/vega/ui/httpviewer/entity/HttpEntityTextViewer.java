package com.subgraph.vega.ui.httpviewer.entity;

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
	void setInput(String text, String contentType) {
		viewer.unconfigure();
		viewer.setDocument(documentFactory.createDocument(text, contentType));
		configureForContentType(contentType);
		viewer.refresh();
	}

	void clear() {
		viewer.setInput(null);
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
