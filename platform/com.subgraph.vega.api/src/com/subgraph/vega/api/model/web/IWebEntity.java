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
package com.subgraph.vega.api.model.web;

import org.apache.http.HttpHost;

import com.subgraph.vega.api.model.IModelProperties;

public interface IWebEntity extends IModelProperties, IWebModelVisitable {
	boolean isVisited();
	void setVisited(boolean notify);
	boolean isScanned();
	void setScanned();
	IWebEntity getParent();
	HttpHost getHttpHost();
}
