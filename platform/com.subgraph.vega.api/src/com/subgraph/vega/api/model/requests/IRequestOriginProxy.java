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

import java.net.InetAddress;

/**
 * Request origin information for the Vega intercepting proxy.
 */
public interface IRequestOriginProxy extends IRequestOrigin {
	/**
	 * Get the internet address of the proxy listener.
	 * @return Proxy listener address.
	 */
	InetAddress getInetAddress();

	/**
	 * Get the port of the proxy listener.
	 * @return Proxy listener port.
	 */
	int getPort();
}
