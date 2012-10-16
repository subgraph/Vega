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
package com.subgraph.vega.internal.model.macros;

import java.util.Collection;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.macros.IHttpMacro;
import com.subgraph.vega.api.model.macros.IHttpMacroModel;
import com.subgraph.vega.api.model.macros.NewMacroEvent;

public class HttpMacroModel implements IHttpMacroModel {
	private ObjectContainer database;
	private final EventListenerManager changeEventManager;

	public HttpMacroModel(ObjectContainer database) {
		this.database = database;
		this.changeEventManager = new EventListenerManager();
	}

	@Override
	public Collection<IHttpMacro> getAllMacros() {
		return database.query(IHttpMacro.class);
	}

	@Override
	public IHttpMacro createMacro() {
		return new HttpMacro();
	}

	@Override
	public void store(IHttpMacro macro) {
		database.store(macro);
		changeEventManager.fireEvent(new NewMacroEvent(macro));
	}

	@Override
	public boolean isMacroStored(IHttpMacro macro) {
		return database.ext().isStored(macro);
	}

	@Override
	public IHttpMacro getMacroByName(final String name) {
		final List<IHttpMacro> results = database.query(new Predicate<IHttpMacro>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IHttpMacro macro) {
				return name.equals(macro.getName());
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
