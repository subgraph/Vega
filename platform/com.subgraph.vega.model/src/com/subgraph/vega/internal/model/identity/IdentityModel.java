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

import java.util.Collection;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.identity.IAuthMethodHttpMacro;
import com.subgraph.vega.api.model.identity.IAuthMethodNtlm;
import com.subgraph.vega.api.model.identity.IAuthMethodRfc2617;
import com.subgraph.vega.api.model.identity.IIdentity;
import com.subgraph.vega.api.model.identity.IIdentityModel;
import com.subgraph.vega.api.model.identity.NewIdentityEvent;

public class IdentityModel implements IIdentityModel {
	private final ObjectContainer database;
	private final EventListenerManager changeEventManager;

	public IdentityModel(ObjectContainer database) {
		this.database = database;
		this.changeEventManager = new EventListenerManager();
	}

	@Override
	public Collection<IIdentity> getAllIdentities() {
		return database.query(IIdentity.class);
	}

	@Override
	public IIdentity createIdentity() {
		return new Identity();
	}

	@Override
	public IAuthMethodRfc2617 createAuthMethodRfc2617() {
		return new AuthMethodRfc2617();
	}

	@Override
	public IAuthMethodNtlm createAuthMethodNtlm() {
		return new AuthMethodNtlm();
	}

	@Override
	public IAuthMethodHttpMacro createAuthMethodHttpMacro() {
		return new AuthMethodHttpMacro();
	}

	@Override
	public void store(IIdentity identity) {
		database.store(identity);
		changeEventManager.fireEvent(new NewIdentityEvent(identity));
	}

	@Override
	public boolean isIdentityStored(IIdentity identity) {
		return database.ext().isStored(identity);
	}

	@Override
	public IIdentity getIdentityByName(final String name) {
		final List<IIdentity> results = database.query(new Predicate<IIdentity>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IIdentity identity) {
				return name.equals(identity.getName());
			}
		});
		if (results.size() == 0) {
			return null;
		}
		return results.get(0);
	}

	@Override
	public void addChangeListener(IEventHandler listener) {
		changeEventManager.addListener(listener);
	}

	@Override
	public void removeChangeListener(IEventHandler listener) {
		changeEventManager.removeListener(listener);
	}

}
