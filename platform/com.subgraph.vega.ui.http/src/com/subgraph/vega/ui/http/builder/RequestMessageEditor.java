package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.ui.httpeditor.parser.HttpRequestParser;
import com.subgraph.vega.ui.text.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.text.httpeditor.RequestRenderer;

/**
 * Manages visual components to edit a HTTP request message.
 */
public class RequestMessageEditor implements IHttpBuilderPart {
	private final IHttpRequestBuilder requestBuilder;
	private final HttpRequestParser requestParser;
	private final RequestRenderer requestRenderer;
	private Composite parentComposite;
	private HttpRequestViewer requestViewer;

	public RequestMessageEditor(final IHttpRequestBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
		requestParser = new HttpRequestParser(this.requestBuilder);
		requestRenderer = new RequestRenderer();
	}
	
	@Override
	public Composite createPartControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.BORDER);
		parentComposite.setLayout(new FillLayout());

		requestViewer = new HttpRequestViewer(parentComposite);

		refresh();

		return parentComposite;
	}

	@Override
	public Control getControl() {
		return parentComposite;
	}

	@Override
	public void refresh() {
		final StringBuilder sb = new StringBuilder();

		final String requestLine = requestBuilder.getRequestLine();
		if (requestLine != null) {
			sb.append(requestLine);
			sb.append('\n');
		}
		
		final IHttpHeaderBuilder[] headers = requestBuilder.getHeaders();
		if (headers.length > 0) {
			for (IHttpHeaderBuilder h: headers) {
				// REVISIT: put raw header here, if applicable
				sb.append(h.getName());
				sb.append(": ");
				sb.append(h.getValue());
				sb.append('\n');
			}
		}

		if (requestLine != null || headers.length != 0) {
			sb.append('\n');
		}
		final HttpEntity entity = requestBuilder.getEntity();
		if (entity != null) {
			sb.append(requestRenderer.renderEntity(requestBuilder.getEntity()));
		}
		
		requestViewer.setContent(sb.toString());
	}

	@Override
	public void processContents() {
		try {
			// REVISIT: the parser should be clearing these 
			requestBuilder.clearHeaders();
			requestBuilder.setEntity(null);
			requestParser.parseRequest(requestViewer.getContent(), null);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage()); // REVISIT: do we really want to throw this?
		}
	}

}
