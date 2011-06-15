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
package com.subgraph.vega.internal.model.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashSet;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.web.IWebPathParameters;

public class WebPathParameters implements IWebPathParameters, Activatable {
	
	private transient Activator activator;

	private final Set<List<NameValuePair>> parameterLists;
	
	WebPathParameters() {
		this.parameterLists = new ActivatableHashSet<List<NameValuePair>>();
	}

	void addParameterList(List<NameValuePair> params) {
		if(params.isEmpty())
			return;
		activate(ActivationPurpose.READ);
		synchronized(parameterLists) {
			if(parameterLists.contains(params))
				return;
			parameterLists.add(params);
		}
	}
	
	public Collection<String> getValuesForParameter(String name) {
		final Set<String> valueSet = new HashSet<String>();
		for(List<NameValuePair> params: getParameterLists()) {
			for(NameValuePair pair: params) {
				if(pair.getName().equals(name)) {
					valueSet.add(pair.getValue());
				}
			}
		}
		return valueSet;		
	}
	
	@Override
	public boolean hasParameters() {
		activate(ActivationPurpose.READ);
		synchronized(parameterLists) {
			return !parameterLists.isEmpty();
		}
	}

	@Override
	public Collection<List<NameValuePair>> getParameterLists() {
		activate(ActivationPurpose.READ);
		synchronized(parameterLists) {
			return new HashSet<List<NameValuePair>>(parameterLists);
		}
	}
	@Override
	public Collection<String> getParameterNames() {
		final Set<String> nameSet = new HashSet<String>();
		for(List<NameValuePair> params: getParameterLists()) {
			for(NameValuePair pair: params) {
				nameSet.add(pair.getName());
			}
		}
		return nameSet;
	}
	
	@Override
	public Collection<List<String>> getParameterNameLists() {
		final Set< List<String> > nameLists = new HashSet<List<String>>();
		for(List<NameValuePair> params: getParameterLists()) {
			List<String> nl = new ArrayList<String>();
			for(NameValuePair pair: params) {
				nl.add(pair.getName());
			}
			nameLists.add(nl);
		}
		return nameLists;
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}		
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;		
	}
}
