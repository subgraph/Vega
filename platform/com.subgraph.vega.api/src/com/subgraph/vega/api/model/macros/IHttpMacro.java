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
package com.subgraph.vega.api.model.macros;

import java.util.Collection;

/**
 * A HTTP macro. A macro is a sequence of one or more requests.
 */
public interface IHttpMacro {	
	/**
	 * Set the name of the macro.
	 * @param name Macro name.
	 */
	void setName(String name);

	/**
	 * Get the name of the macro.
	 * @param name Macro name.
	 */
	String getName();

	/**
	 * Get the macro items comprising this macro. The collection is ordered by item execution order.
	 * @return Macro items. 
	 */
	Collection<IHttpMacroItem> getMacroItems();
}
