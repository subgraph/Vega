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
package com.subgraph.vega.application.about;

public class BrowseSubgraphHandler extends AbstractURLOpenHandler {
	private final static String URL_STRING = "http://www.subgraph.com";

	public BrowseSubgraphHandler() {
		super(URL_STRING);
	}
}
