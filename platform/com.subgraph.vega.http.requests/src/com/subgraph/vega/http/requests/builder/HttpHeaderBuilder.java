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

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;

public class HttpHeaderBuilder implements IHttpHeaderBuilder {
	private String name;
	private String value;
	
	public HttpHeaderBuilder(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public HttpHeaderBuilder(Header header) {
		name = header.getName();
		value = header.getValue();
	}

	@Override
	public void setFromHeader(Header header) {
		name = header.getName();
		value = header.getValue();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Header buildHeader() {
		return new BasicHeader(name, value);
	}

}
