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

import com.subgraph.vega.api.events.IEventHandler;

public interface IHttpMacroModel {
	/**
	 * Get all macros managed by the model.
	 * @return Identities managed by the model.
	 */
	Collection<IHttpMacro> getAllMacros();

	/**
	 * Create a new macro with nothing set. The macro is not stored to the database.
	 * @return New macro.
	 */
	IHttpMacro createMacro();

	/**
	 * Store a macro in the workspace. The caller is responsible for ensuring an IHttpMacro with the same name does
	 * not already exist.
	 * @param macro Macro to store.
	 */
	void store(IHttpMacro macro);

	/**
	 * Determine whether an macro exists in the workspace.
	 * @param macro Macro.
	 * @return Boolean indicating whether the macro exists in the workspace.
	 */
	boolean isMacroStored(IHttpMacro macro);

	/**
	 * Lookup an macro in the workspace by name. The lookup is case-insensitive.
	 * @param name Macro, or null if none exists with the given name.
	 */
	IHttpMacro getMacroByName(String name);

	/**
	 * Register an event listener to watch for changes to identities managed by the model. Fires:
	 * 	- NewMacroEvent
	 * @param listener Event listener.
	 */
	void addChangeListener(IEventHandler listener);

	/**
	 * Deregister a change event listener.
	 * @param listener Event listener.
	 */
	void removeChangeListener(IEventHandler listener);
}
