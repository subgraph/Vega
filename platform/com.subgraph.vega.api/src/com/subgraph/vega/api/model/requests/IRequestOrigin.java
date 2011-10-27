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
package com.subgraph.vega.api.model.requests;

/**
 * Describes the origin of a request within Vega, i.e. which module generated a request, along with optional additional
 * information about the origin.
 */ 
public interface IRequestOrigin {
	/**
	 * Enumeration of request origins.
	 */
	public enum Origin {
		ORIGIN_SCANNNER, /** IRequestOriginScanner */
		ORIGIN_PROXY, /** IRequestOriginProxy */
		ORIGIN_REQUEST_EDITOR,
	};

	/**
	 * Get the origin.
	 * @return Origin.
	 */
	Origin getOrigin();	
}
