package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;
import com.subgraph.vega.ui.text.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.text.httpeditor.RequestRenderer;

/**
 * Manages visual components to edit the entity body of a HTTP message.
 */
public class BodyEditor implements IHttpBuilderPart {
	private IHttpMessageBuilder messageBuilder;
	private Composite parentComposite;
	private HttpRequestViewer requestBodyViewer;
	private final RequestRenderer requestRenderer = new RequestRenderer();	

	public BodyEditor(final IHttpMessageBuilder messageBuilder) {
		this.messageBuilder = messageBuilder;
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
		if (messageBuilder.getEntity() != null) {
			requestBodyViewer.setContent(requestRenderer.renderEntity(messageBuilder.getEntity()));
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
			messageBuilder.setEntity(entity);
		} else {
			messageBuilder.setEntity(null);
		}
	}

}
