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
package com.subgraph.vega.internal.model.variables;

import java.util.Collection;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.model.variables.IVariable;
import com.subgraph.vega.api.model.variables.IVariableModel;

public class VariableModel implements IVariableModel {
	private ObjectContainer database;

	public VariableModel(ObjectContainer database) {
		this.database = database;
	}

	@Override
	public Collection<IVariable> getAllIdentities() {
		return database.query(IVariable.class);
	}

	@Override
	public IVariable createVariable() {
		return new Variable();
	}

	@Override
	public void store(IVariable variable) {
		database.store(variable);
	}

	@Override
	public boolean isVariableStored(IVariable variable) {
		return database.ext().isStored(variable);
	}

	@Override
	public IVariable getVariableByName(final String name) {
		final List<IVariable> results = database.query(new Predicate<IVariable>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IVariable tag) {
				return name.equals(tag.getName());
			}
		});
		if (results.size() == 0) {
			return null;
		}
		return results.get(0);
	}

}
