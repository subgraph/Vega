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
package com.subgraph.vega.ui.http.preferencepage;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;

public class ListenerTableContentProvider implements IStructuredContentProvider {
	private List<IHttpProxyListenerConfig> addressList;
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		addressList = (List<IHttpProxyListenerConfig>) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return addressList.toArray();
	}

}
