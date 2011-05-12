package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.ui.httpeditor.parser.HttpRequestParser;
import com.subgraph.vega.ui.httpviewer.HttpMessageViewer;

/**
 * Manages visual components to edit a HTTP request message.
 */
public class RequestMessageEditor implements IHttpBuilderPart {
	private final IHttpRequestBuilder builder;
	private final HttpRequestParser requestParser;
	private HttpMessageViewer messageViewer;

	public RequestMessageEditor(final IHttpRequestBuilder builder) {
		this.builder = builder;
		requestParser = new HttpRequestParser(this.builder, false);
	}
	
	@Override
	public Composite createPartControl(Composite parent) {
		messageViewer = new HttpMessageViewer(parent);
		messageViewer.setEditable(true);
		messageViewer.setDisplayImages(true);
		messageViewer.setDisplayImagesAsHex(true);
		refresh();
		return messageViewer;
	}

	@Override
	public Control getControl() {
		return messageViewer;
	}

	@Override
	public void refresh() {
		messageViewer.displayHttpRequest(builder);
	}

	@Override
	public void processContents() {
		// REVISIT: the parser should be clearing these 
		builder.clearHeaders();

		try {
			requestParser.parseRequest(messageViewer.getContent());
			if (messageViewer.isEntityContentDirty()) {
				builder.setEntity(messageViewer.getEntityContent());
//			} else {
//				builder.setEntity(null);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
		}
	}

}
