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

public interface IVariable {
	/**
	 * Set the variable name. The variable name cannot be null.
	 * @param name Variable name.
	 */
	void setName(String name);
	
	/**
	 * Get the variable name.
	 * @return Variable name.
	 */
	String getName();

	/**
	 * Set a description for the variable.
	 * @param description Variable description.
	 */
	void setDescription(String description);

	/**
	 * Get the description for the variable.
	 * @return Description of the variable, or null if none is set.
	 */
	String getDescription();
}
