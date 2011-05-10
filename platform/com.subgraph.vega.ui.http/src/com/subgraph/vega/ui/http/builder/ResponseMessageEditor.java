package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpeditor.parser.HttpResponseParser;
import com.subgraph.vega.ui.httpviewer.HttpMessageViewer;

/**
 * Manages visual components to edit a HTTP response message.
 */
public class ResponseMessageEditor implements IHttpBuilderPart {
	private final IHttpResponseBuilder builder;
	private final HttpResponseParser responseParser;
	private HttpMessageViewer messageViewer;

	public ResponseMessageEditor(final IHttpResponseBuilder builder) {
		this.builder = builder;
		responseParser = new HttpResponseParser(this.builder);
	}
	
	@Override
	public Composite createPartControl(Composite parent) {
		messageViewer = new HttpMessageViewer(parent);
		messageViewer.setEditable(true);
		refresh();
		return messageViewer;
	}

	@Override
	public Control getControl() {
		return messageViewer;
	}

	@Override
	public void refresh() {
		messageViewer.displayHttpResponse(builder);
	}

	@Override
	public void processContents() {
		// REVISIT: the parser should be clearing these 
		builder.clearHeaders();
		builder.setEntity(null);

		try {
			responseParser.parseResponse(messageViewer.getContent());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
		}
	}

}
