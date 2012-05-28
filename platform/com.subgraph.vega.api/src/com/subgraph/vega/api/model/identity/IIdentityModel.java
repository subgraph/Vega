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

import com.subgraph.vega.api.events.IEventHandler;

public interface IIdentityModel {
	/**
	 * Get all identities managed by the model.
	 * @return Identities managed by the model.
	 */
	Collection<IIdentity> getAllIdentities();

	/**
	 * Create a new identity with no fields set. The identity is not stored to the database.
	 * @return New identity.
	 */
	IIdentity createIdentity();

	/**
	 * Create an IAuthMethodRfc2617 instance with no fields set. The IAuthMethod is not associated with any identity. 
	 * @return IAuthMethodRfc2617.
	 */
	IAuthMethodRfc2617 createAuthMethodRfc2617();

	/**
	 * Create an IAuthMethodNtlm instance with no fields set. The IAuthMethod is not associated with any identity. 
	 * @return IAuthMethodNtlm.
	 */
	IAuthMethodNtlm createAuthMethodNtlm();

	/**
	 * Create an IAuthMethodHttpMacro instance with no fields set. The IAuthMethod is not associated with any identity.
	 * @return IAuthMethodHttpMacro.
	 */
	IAuthMethodHttpMacro createAuthMethodHttpMacro();

	/**
	 * Store an identity in the workspace. The caller is responsible for ensuring an IIdentity with the same name does
	 * not already exist.
	 * @param scanIdentity Scan identity to store.
	 */
	void store(IIdentity scanIdentity);

	/**
	 * Determine whether an identity exists in the workspace.
	 * @param identity Identity.
	 * @return Boolean indicating whether the identity exists in the workspace.
	 */
	boolean isIdentityStored(IIdentity identity);

	/**
	 * Lookup an identity in the workspace by name. The lookup is case-insensitive.
	 * @param name Identity, or null if none exists with the given name.
	 */
	IIdentity getIdentityByName(String name);

	/**
	 * Register an event listener to watch for changes to identities managed by the model. Fires:
	 * 	- NewIdentityEvent
	 * @param listener Event listener.
	 */
	void addChangeListener(IEventHandler listener);

	/**
	 * De4register a change event listener.
	 * @param listener Event listener.
	 */
	void removeChangeListener(IEventHandler listener);
}
