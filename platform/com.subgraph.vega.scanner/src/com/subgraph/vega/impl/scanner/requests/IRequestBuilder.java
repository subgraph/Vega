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
package com.subgraph.vega.impl.scanner.requests;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;

public interface IRequestBuilder {
	HttpUriRequest createBasicRequest();
	HttpUriRequest createAlteredRequest(String value, boolean append);
	HttpUriRequest createAlteredParameterNameRequest(String name);
	boolean isFuzzable();
	NameValuePair getFuzzableParameter();
}
