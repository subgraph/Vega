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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

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
	 * Create an IHttpMacroItem and add it to this macro.
	 * @param record IRequestLogRecord to create the item for.
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	IHttpMacroItem createMacroItem(IRequestLogRecord record) throws URISyntaxException, IOException;

	/**
	 * Remove a macro item.
	 * @param item Macro item.
	 */
	void removeMacroItem(IHttpMacroItem item);
	
	/**
	 * Get the macro items comprising this macro. The collection is ordered by item execution order.
	 * @return Macro items. 
	 */
	Collection<IHttpMacroItem> getMacroItems();

	/**
	 * Returns the number of macro items in this macro.
	 * @return The number of macro items.
	 */
	int macroItemsSize();
	
	/**
	 * Get the array index of a macro item.
	 * @param item Macro item.
	 * @return Array index, or -1.
	 */
	int indexOfMacroItem(IHttpMacroItem item);
	
	/**
	 * Swap macro items at two positions.
	 * @return Swapped macro items. 
	 */
	void swapMacroItems(int idx1, int idx2);
	
}
