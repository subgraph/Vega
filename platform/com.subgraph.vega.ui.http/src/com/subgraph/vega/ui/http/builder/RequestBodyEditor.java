package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.ui.text.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.text.httpeditor.RequestRenderer;

/**
 * Manages visual components to edit the entity body of a HTTP request message.
 */
public class RequestBodyEditor implements IHttpBuilderPart {
	private IHttpRequestBuilder requestBuilder;
	private Composite parentComposite;
	private HttpRequestViewer requestBodyViewer;
	private final RequestRenderer requestRenderer = new RequestRenderer();	

	public RequestBodyEditor(final IHttpRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}

	@Override
	public Composite createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new FillLayout());

		requestBodyViewer = new HttpRequestViewer(parentComposite);

		refresh();

		return parentComposite;
	}

	@Override
	public Control getControl() {
		return parentComposite;
	}

	@Override
	public void refresh() {
		if (requestBuilder.getEntity() != null) {
			requestBodyViewer.setContent(requestRenderer.renderEntity(requestBuilder.getEntity()));
		}
	}

	@Override
	public void processContents() {
		final String body = requestBodyViewer.getContent();
		if (body.length() != 0) {
			StringEntity entity;
			try {
				entity = new StringEntity(body);
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
			}
			requestBuilder.setEntity(entity);
		} else {
			requestBuilder.setEntity(null);
		}
	}

}
