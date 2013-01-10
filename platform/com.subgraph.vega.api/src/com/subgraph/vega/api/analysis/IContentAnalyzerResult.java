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
package com.subgraph.vega.api.analysis;

import java.util.List;

import com.subgraph.vega.api.util.VegaURI;

public interface IContentAnalyzerResult {
	List<VegaURI> getDiscoveredURIs();
	MimeType getDeclaredMimeType();
	MimeType getSniffedMimeType();
}
