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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpeditor.HttpMessageEditor;
import com.subgraph.vega.ui.httpeditor.parser.HttpResponseParser;

/**
 * Manages visual components to edit a HTTP response message.
 */
public class ResponseMessageEditor extends Composite implements IHttpBuilderPart {
	private final IHttpResponseBuilder builder;
	private final HttpResponseParser responseParser;
	private HttpMessageEditor messageViewer;

	public ResponseMessageEditor(Composite parent, final IHttpResponseBuilder builder) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		this.builder = builder;
		responseParser = new HttpResponseParser(this.builder, false);

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
		messageViewer.displayHttpResponse(builder);
	}

	@Override
	public void processContents() throws BuilderParseException {
		try {
			responseParser.parseResponse(messageViewer.getContent());
			if (messageViewer.isEntityContentDirty()) {
				builder.setEntity(messageViewer.getEntityContent());
			}
		} catch (UnsupportedEncodingException e) {
			throw new BuilderParseException("Error getting entity", e);
		}
	}

}
