/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.http.builder;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.ui.httpeditor.HttpMessageEditor;
import com.subgraph.vega.ui.httpeditor.parser.HttpRequestParser;

/**
 * Manages visual components to edit a HTTP request message.
 */
public class RequestMessageEditor extends Composite implements IHttpBuilderPart {
	private final IHttpRequestBuilder builder;
	private final HttpRequestParser requestParser;
	private HttpMessageEditor messageViewer;

	public RequestMessageEditor(Composite parent, final IHttpRequestBuilder builder) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		this.builder = builder;
		requestParser = new HttpRequestParser(this.builder, false);

		messageViewer = new HttpMessageEditor(this);
		messageViewer.setEditable(true);
		messageViewer.setDisplayImages(true);
		messageViewer.setDisplayImagesAsHex(true);
		refresh();
	}
	
	@Override
	public Control getControl() {
		return this;
	}

	@Override
	public void setEditable(boolean editable) {
		messageViewer.setEditable(editable);
	}

	@Override
	public void refresh() {
		messageViewer.displayHttpRequest(builder);
	}

	@Override
	public void processContents() throws BuilderParseException {
		try {
			requestParser.parseRequest(messageViewer.getContent());
			if (messageViewer.isEntityContentDirty()) {
				builder.setEntity(messageViewer.getEntityContent());
			}
		} catch (UnsupportedEncodingException e) {
			throw new BuilderParseException("Error getting entity", e);
		} catch (URISyntaxException e) {
			throw new BuilderParseException("Error parsing URI", e);
		}
	}

}
