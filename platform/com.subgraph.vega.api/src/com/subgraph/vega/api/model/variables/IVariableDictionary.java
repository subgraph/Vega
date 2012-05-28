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

import java.util.Map;

public interface IVariableDictionary {
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
	 * Get a copy of the dictionary as a map.
	 * @return Dictionary.
	 */
	Map<String, String> getDict();
}
