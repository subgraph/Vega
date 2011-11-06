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
package com.subgraph.vega.api.model.variables;

import java.util.Collection;

import com.subgraph.vega.api.model.variables.IVariable;

public interface IVariableModel {
	/**
	 * Get all identities managed by the model.
	 * @return Identities managed by the model.
	 */
	Collection<IVariable> getAllIdentities();

	/**
	 * Create a new variable with no fields set. The variable is not stored to the database.
	 * @return New variable.
	 */
	IVariable createVariable();
	
	/**
	 * Store an variable in the workspace. The caller is responsible for ensuring an IVariable with the same name does
	 * not already exist.
	 * @param variable Variable to store.
	 */
	void store(IVariable variable);

	/**
	 * Determine whether an variable exists in the workspace.
	 * @param variable Variable.
	 * @return Boolean indicating whether the variable exists in the workspace.
	 */
	boolean isVariableStored(IVariable variable);

	/**
	 * Lookup an variable in the workspace by name. The lookup is case-insensitive.
	 * @param name Variable, or null if none exists with the given name.
	 */
	IVariable getVariableByName(String name);
}
