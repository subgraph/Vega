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

import com.subgraph.vega.api.model.macros.IHttpMacro;

public interface IAuthMethodHttpMacro extends IAuthMethod {
	/**
	 * Set the HTTP macro to be used for authentication.
	 * @param macro Authentication macro.
	 */
	void setMacro(IHttpMacro macro);

	/**
	 * Get the HTTP macro to be used for authentication.
	 * @return Authentication macro.
	 */
	IHttpMacro getMacro();
}
