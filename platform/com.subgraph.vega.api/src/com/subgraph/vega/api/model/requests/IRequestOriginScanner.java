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
 * Request origin information for the scanner.
 */
public interface IRequestOriginScanner extends IRequestOrigin {
	/**
	 * Get the ID of the IScanInstance that generated the request.
	 * @return IScanInstance ID.
	 */
	long getScanId();
}
