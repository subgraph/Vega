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
package com.subgraph.vega.api.model.identity;

import java.util.Collection;

/**
 * An identity consists of a dictionary of information about a website identity (typical ex. a user on a website) and
 * additional information about how to interact with a site when using that identity.
 */
public interface IIdentity {
	/**
	 * Set the name of this identity. The identity name must be unique within the workspace.
	 * @param name Identity name.
	 */
	void setName(String name);

	/**
	 * Get the name of this identity.
	 * @return Identity name.
	 */
	String getName();
	
	/**
	 * Set the authentication method for this identity.
	 * @param authMethod Authentication method.
	 */
	void setAuthMethod(IAuthMethod authMethod);
	
	/**
	 * Get the authentication method for this identity.
	 * @return Authentication method.
	 */
	IAuthMethod getAuthMethod();
	
	/**
	 * Set a dictionary value.
	 * @param key IVariable name to set.
	 * @param value Value to set.
	 * @return Previous value, or null if none was set.
	 */
	String setDictValue(String key, String value);

	/**
	 * Get a dictionary value for a variable name.
	 * @param key IVariable name.
	 * @return Dictionary value, or null if none exists.
	 */
	String getDictValue(String key);

	/**
	 * Add a path exclusion regular expression to prevent the scanner from accessing paths. 
	 * @param expression Path exclusion, a regular expression.
	 */
	void addPathExclusion(String expression);

	/**
	 * Get all path exclusion expressions for this identity.
	 * @return Path exclusion expressions for this identity.
	 */
	Collection<String> getPathExclusions();

	/**
	 * Remove a path exclusion.
	 * @param expression Path exclusion expression.
	 */
	void rmPathExclusion(String expression);
}
