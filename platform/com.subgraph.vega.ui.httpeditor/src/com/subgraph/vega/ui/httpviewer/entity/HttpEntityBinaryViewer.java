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
package com.subgraph.vega.ui.httpviewer.entity;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.ui.hexeditor.HexEditControl;

public class HttpEntityBinaryViewer extends Composite {
	private final HexEditControl hexedit;
	
	private String inputContentType;
	private String inputContentEncoding;
	
	public HttpEntityBinaryViewer(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		hexedit = new HexEditControl(this);
	}
		
	void setInput(byte[] data, String contentType, String contentEncoding) {
		inputContentType = contentType;
		inputContentEncoding = contentEncoding;
		hexedit.setInput(data);
	}
	
	void clear() {
		inputContentType = null;
		inputContentEncoding = null;
		hexedit.setInput(new byte[0]);
	}
	
	boolean isContentDirty() {
		return hexedit.isContentDirty();
	}
	
	HttpEntity getEntityContent() {
		final byte[] content = hexedit.getContent();
		if(content == null) {
			return null;
		} else {
			return createEntity(content, inputContentType, inputContentEncoding);
		}
	}
	
	private HttpEntity createEntity(byte[] content, String contentType, String contentEncoding) {
		final ByteArrayEntity entity = new ByteArrayEntity(content);
		if(contentType != null && !contentType.isEmpty()) {
			entity.setContentType(contentType);
		}
		if(contentEncoding != null && !contentEncoding.isEmpty()) {
			entity.setContentEncoding(contentEncoding);
		}
		return entity;		
	}
}
