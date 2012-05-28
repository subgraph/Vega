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

import com.subgraph.vega.api.http.requests.IHttpRequestEngine;

/**
 * Base interface for an authentication method used to authenticate an identity with a website.
 */
public interface IAuthMethod {
	enum AuthMethodType {
		/**
		 * Basic and digest access authentication as defined in RFC2617. IAuthMethodRfc2617.
		 */
		AUTH_METHOD_RFC2617,

		/**
		 * NTLM and NTLMv2. IAuthMethodNtlm.
		 */
		AUTH_METHOD_NTLM,
		
		/**
		 * HTTP macro.
		 */
		AUTH_METHOD_HTTP_MACRO,
	}

	/**
	 * Get the authentication method type.
	 * @return Authentication method type.
	 */
	AuthMethodType getType();
	
	/**
	 * Set up authentication parameters within a request engine.
	 * @param requestEngine Request engine.
	 */
	void setAuth(IHttpRequestEngine requestEngine);
}
