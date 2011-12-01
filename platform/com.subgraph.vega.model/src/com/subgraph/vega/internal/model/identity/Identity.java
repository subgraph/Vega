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
package com.subgraph.vega.internal.model.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IIdentity;

public class Identity implements IIdentity, Activatable {
	private transient Activator activator;
	private String name;
	private IAuthMethod authMethod;
	private ActivatableHashMap<String, String> userDict;
	private ActivatableArrayList<String> pathExclusionList;

	public Identity() {
		pathExclusionList = new ActivatableArrayList<String>();
		userDict = new ActivatableHashMap<String, String>(); 
	}

	@Override
	public void setName(String name) {
		activate(ActivationPurpose.READ);
		this.name = name;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public void setAuthMethod(IAuthMethod authMethod) {
		activate(ActivationPurpose.READ);
		// XXX remove the old one from the database?
		this.authMethod = authMethod;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public IAuthMethod getAuthMethod() {
		activate(ActivationPurpose.READ);
		return authMethod;
	}

	@Override
	public void addPathExclusion(String expression) {
		activate(ActivationPurpose.READ);
		pathExclusionList.add(expression);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public Collection<String> getPathExclusions() {
		activate(ActivationPurpose.READ);
		return new ArrayList<String>(pathExclusionList);
	}

	@Override
	public void rmPathExclusion(String expression) {
		activate(ActivationPurpose.READ);
		pathExclusionList.remove(expression);
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String setDictValue(String key, String value) {
		activate(ActivationPurpose.READ);
		return userDict.put(key, value);
	}

	@Override
	public String getDictValue(String key) {
		activate(ActivationPurpose.READ);
		return userDict.get(key);
	}

	@Override
	public Map<String, String> getDict() {
		activate(ActivationPurpose.READ);
		return new HashMap<String, String>(userDict);
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}

}
