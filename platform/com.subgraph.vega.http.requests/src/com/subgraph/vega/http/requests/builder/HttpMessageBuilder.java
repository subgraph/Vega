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
package com.subgraph.vega.http.requests.builder;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpMessageBuilder;

public class HttpMessageBuilder implements IHttpMessageBuilder {
	private HttpParams params;
	private ProtocolVersion protocolVersion;
	private final ArrayList<HttpHeaderBuilder> headerList = new ArrayList<HttpHeaderBuilder>();
	private HttpEntity entity;

	@Override
	public void clear() {
		params = null;
		protocolVersion = null;
		headerList.clear();
		entity = null;
	}

	@Override
	public void setParams(HttpParams params) {
		this.params = params;
	}

	@Override
	public HttpParams getParams() {
		return params;
	}

	@Override
	public void setProtocolVersion(ProtocolVersion protocolVersion) {
		this.protocolVersion = protocolVersion; 
	}

	@Override
	public ProtocolVersion getProtocolVersion() {
		return protocolVersion;
	}

	@Override
	public void setHeaders(Header[] headers) {
		headerList.clear();
		for (Header h: headers) {
			headerList.add(new HttpHeaderBuilder(h));
		}
	}
	
	@Override
	public HttpHeaderBuilder addHeader(String name, String value) {
		HttpHeaderBuilder header = new HttpHeaderBuilder(name, value);
		headerList.add(header);
		return header;
	}

	@Override
	public HttpHeaderBuilder setHeader(String name, String value) {
        for (int i = 0; i < headerList.size(); i++) {
        	HttpHeaderBuilder h = headerList.get(i);
            if (name.equalsIgnoreCase(h.getName())) {
            	h.setName(name);
            	h.setValue(value);
            	return h;
            }
        }
        return addHeader(name, value);
	}

	@Override
	public void removeHeader(IHttpHeaderBuilder header) {
		headerList.remove(header);
	}

	@Override
	public void removeHeaders(final String name) {
        for (Iterator<HttpHeaderBuilder> i = headerList.iterator(); i.hasNext(); ) {
        	IHttpHeaderBuilder header = i.next();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
	}
	
	@Override
	public void clearHeaders() {
		headerList.clear();
	}
	
	@Override
	public void swapHeader(int idx1, int idx2) {
		if (idx1 < headerList.size() && idx2 < headerList.size() && idx1 != idx2) {
			HttpHeaderBuilder tmp = headerList.set(idx1, headerList.get(idx2));
			headerList.set(idx2, tmp);
		}
	}
	
	@Override
	public int getHeaderIdxOf(IHttpHeaderBuilder header) {
		return headerList.indexOf(header);
	}

	@Override
	public int getHeaderCnt() {
		return headerList.size();
	}

	@Override
	public IHttpHeaderBuilder getHeader(int idx) {
		return headerList.get(idx);
	}

	@Override
	public IHttpHeaderBuilder[] getHeaders() {
		return headerList.toArray(new HttpHeaderBuilder[headerList.size()]);
	}

	@Override
	public void setEntity(HttpEntity entity) {
		this.entity = entity;
		setHeadersEntity();
	}

	@Override
	public HttpEntity getEntity() {
		return entity;
	}

	protected void setHeadersEntity() {
		if (entity != null) {
	        if (entity.isChunked() || entity.getContentLength() < 0) {
                setHeader(HTTP.TRANSFER_ENCODING, HTTP.CHUNK_CODING);
                removeHeaders(HTTP.CONTENT_LEN);
	        } else {
                setHeader(HTTP.CONTENT_LEN, Long.toString(entity.getContentLength()));
                removeHeaders(HTTP.TRANSFER_ENCODING);
	        }

            if (entity.getContentType() != null) {
            	final Header h = entity.getContentType();  
                setHeader(h.getName(), h.getValue());
            }

            if (entity.getContentEncoding() != null) {
            	final Header h = entity.getContentEncoding();  
                setHeader(h.getName(), h.getValue());
            }
		} else {
            removeHeaders(HTTP.CONTENT_LEN);
            removeHeaders(HTTP.TRANSFER_ENCODING);
		}
	}

}
