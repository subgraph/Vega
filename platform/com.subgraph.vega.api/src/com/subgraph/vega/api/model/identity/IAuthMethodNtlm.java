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

/**
 * NTLMv1, NTLMv2, and NTLM2 session authentication.
 */
public interface IAuthMethodNtlm extends IAuthMethod {
	/**
	 * Set the username. This should not include the domain to authenticate with. For example: "user" is correct whereas
	 * "DOMAIN\\user" is not.
	 * @param username Username.
	 */
	void setUsername(String username);
	
	/**
	 * Get the username.
	 * @return Username.
	 */
	String getUsername();
	
	/**
	 * Set the password.
	 * @param password Password.
	 */
	void setPassword(String password);
	
	/**
	 * Get the password.
	 * @return Password.
	 */
	String getPassword();
	
	/**
	 * Set the name of the workstation the authentication requests will originate from. Essentially, the computer name
	 * for this machine.
	 * @param workstation Workstation name.
	 */
	void setWorkstation(String workstation);

	/**
	 * Get the name of the workstation the authentication requests will originate from.
	 * @return Workstation name.
	 */
	String getWorkstation();
	
	/**
	 * Set the domain to authenticate with.
	 * @param domain Domain to authenticate with.
	 */
	void setDomain(String domain);

	/**
	 * Get the domain to authenticate with.
	 * @return Domain to authenticate with.
	 */
	String getDomain();
}
