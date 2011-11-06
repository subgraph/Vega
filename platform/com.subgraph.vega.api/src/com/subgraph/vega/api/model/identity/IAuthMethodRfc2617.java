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

public interface IAuthMethodRfc2617 extends IAuthMethod {
	enum AuthScheme {
		/**
		 * Basic authentication scheme as described by RFC2617. Sends authentication information as cleartext.
		 */
		AUTH_SCHEME_BASIC,
		
		/**
		 * Digest authentication scheme as described by RFC2617. Hashes the password with a shared secret before it is
		 * sent.
		 */
		AUTH_SCHEME_DIGEST,
	};

	/**
	 * Set the authentication scheme. The scheme is set to AUTH_SCHEME_DIGEST by default.
	 * @param authScheme Authentication scheme. 
	 */
	void setAuthScheme(AuthScheme authScheme);

	/**
	 * Get the authentication scheme.
	 * @return Authentication scheme.
	 */
	AuthScheme getAuthScheme();
	
	/**
	 * Set the username to use when authenticating.
	 * @param username Username.
	 */
	void setUsername(String username);
	
	/**
	 * Get the username to use when authenticating.
	 * @return Username.
	 */
	String getUsername();
	
	/**
	 * Set the password to use when authenticating.
	 * @param password Password.
	 */
	void setPassword(String password);

	/**
	 * Get the password to use when authenticating.
	 * @return Password.
	 */
	String getPassword();

	/**
	 * Set the charset to be used when encoding user credentials. Set to "US-ASCII" by default.
	 * @param charset Character set.
	 */
	void setCharset(String charset);

	/**
	 * Get the charset to be used when encoding user credentials.
	 * @return Character set.
	 */
	String getCharset();
}
