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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;
import com.subgraph.vega.api.model.web.IWebPath;

public abstract class AbstractParameterRequestBuilder extends AbstractRequestBuilder {

	protected final List<NameValuePair> parameters;
	protected final int parameterFuzzIndex;
	
	protected AbstractParameterRequestBuilder(IHttpRequestEngine requestEngine, IWebPath path, List<NameValuePair> parameters, int index) {
		super(requestEngine, path);
		if(parameters == null || index < 0 || index >= parameters.size())
			throw new IllegalArgumentException();
		
		this.parameters = parameters;
		this.parameterFuzzIndex = index;
	}
	
	@Override
	public boolean isFuzzable() {
		return true;
	}

	@Override
	public NameValuePair getFuzzableParameter() {
		return parameters.get(parameterFuzzIndex);
	}
	
	protected List<NameValuePair> getAlteredParameters(String fuzzValue, boolean append, boolean fuzzName) {
		final List<NameValuePair> result = new ArrayList<NameValuePair>();
		for(int i = 0; i < parameters.size(); i++) {
			NameValuePair p = parameters.get(i);
			if(parameterFuzzIndex == i)
				result.add(fuzzParameter(p, fuzzValue, append, fuzzName));
			else
				result.add(p);
		}
		return result;
	}

	protected NameValuePair fuzzParameter(NameValuePair param, String fuzzValue, boolean append, boolean fuzzName) {
		if(fuzzName) {
			return new BasicNameValuePair(fuzzValue, param.getValue());
		} else if(append) {
			return new BasicNameValuePair(param.getName(), param.getValue() + fuzzValue);
		} else {
			return new BasicNameValuePair(param.getName(), fuzzValue);
		}
	}
}
