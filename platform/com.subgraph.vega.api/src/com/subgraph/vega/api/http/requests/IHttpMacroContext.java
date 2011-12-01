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
package com.subgraph.vega.api.http.requests;

import java.util.Map;

public interface IHttpMacroContext {
	/**
	 * Set the variable dictionary for this macro context.
	 * @param dict Variable dictionary.
	 */
	void setDict(Map<String, String> dict);
	
	/**
	 * Get the variable dictionary for this macro context.
	 * @return Variable dictionary, or null if none is set.
	 */
	Map<String, String> getDict();
}
