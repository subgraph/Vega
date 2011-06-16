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
package com.subgraph.vega.internal.model.requests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class RequestLogEntity implements HttpEntity {

	private byte[] content;
	private Header contentType;
	private Header contentEncoding;
		
	RequestLogEntity(byte[] content, Header contentType, Header contentEncoding) {
		this.content = content;
		this.contentType = copyHeader(contentType);
		this.contentEncoding = copyHeader(contentEncoding);
	}

	public byte[] getContentArray() {
		return content;
	}

	private Header copyHeader(Header h) {
		if(h == null) {
			return null;
		} else {
			return new BasicHeader(h.getName(), h.getValue());
		}
	}

	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public long getContentLength() {
		return content.length;
	}

	@Override
	public Header getContentType() {
		return contentType;
	}

	@Override
	public Header getContentEncoding() {
		return contentEncoding;
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		if(outstream == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}
		outstream.write(content);
		outstream.flush();
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void consumeContent() throws IOException {		
	}
}
