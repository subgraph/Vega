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
package com.subgraph.vega.internal.model.requests;

import java.net.InetAddress;

import com.db4o.activation.ActivationPurpose;
import com.subgraph.vega.api.model.requests.IRequestOrigin;
import com.subgraph.vega.api.model.requests.IRequestOriginProxy;

public class RequestOriginProxy extends RequestOrigin implements IRequestOriginProxy {
	private InetAddress address;
	private int port;

	public RequestOriginProxy(InetAddress address, int port) {
		super(IRequestOrigin.Origin.ORIGIN_PROXY);
		this.address = address;
		this.port = port;
	}

	@Override
	public InetAddress getInetAddress() {
		activate(ActivationPurpose.READ);
		return address;
	}

	@Override
	public int getPort() {
		activate(ActivationPurpose.READ);
		return port;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();
		buf.append('[');
		buf.append(address.getHostAddress());
		buf.append("]:");
		buf.append(port);
		return buf.toString();
	}

}
