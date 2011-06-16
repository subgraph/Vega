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
package com.subgraph.vega.internal.analysis;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.subgraph.vega.api.analysis.IContentAnalyzerResult;
import com.subgraph.vega.api.analysis.MimeType;

public class ContentAnalyzerResult implements IContentAnalyzerResult {
	private final Set<URI> uniqueUris = new HashSet<URI>();
	
	private MimeType declaredType = MimeType.MIME_NONE;
	private MimeType sniffedType = MimeType.MIME_NONE;
	
	void addUri(URI uri) {
		uniqueUris.add(uri);
	}

	void setDeclaredMimeType(MimeType mime) {
		declaredType = mime;
	}
	
	void setSniffedMimeType(MimeType mime) {
		sniffedType = mime;
	}
	
	@Override
	public List<URI> getDiscoveredURIs() {
		return new ArrayList<URI>(uniqueUris);
	}

	@Override
	public MimeType getDeclaredMimeType() {
		return declaredType;
	}

	@Override
	public MimeType getSniffedMimeType() {
		return sniffedType;
	}
}
