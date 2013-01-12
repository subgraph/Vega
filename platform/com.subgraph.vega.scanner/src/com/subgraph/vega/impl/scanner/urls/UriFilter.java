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
package com.subgraph.vega.impl.scanner.urls;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.util.VegaURI;

public class UriFilter {
	private final IScannerConfig scannerConfig;
	private final Set<VegaURI> visitedURIs = new HashSet<VegaURI>();
	private final ArrayList<Pattern> exclusionList = new ArrayList<Pattern>();

	public UriFilter(IScannerConfig scannerConfig) {
		this.scannerConfig = scannerConfig;
		// REVISIT: we should really be merging identity config into scan config when scan starts
		if (scannerConfig.getScanIdentity() != null) {
			for (String exclusion: scannerConfig.getScanIdentity().getPathExclusions()) {
				exclusionList.add(Pattern.compile(exclusion));
			}
		}
	}

	public boolean isExcluded(final VegaURI uri) {
		for (Pattern p: exclusionList) {
			if (p.matcher(uri.toString()).find() == true) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAllowed(VegaURI uri) {
		final ITargetScope scanTargetScope = scannerConfig.getScanTargetScope();
		return scanTargetScope.filter(uri.toURI()) && !isExcluded(uri);
	}

	public synchronized boolean filter(VegaURI uri) {
		if(visitedURIs.contains(uri) || !isAllowed(uri))
			return false;
		visitedURIs.add(uri);
		return true;
	}
}
