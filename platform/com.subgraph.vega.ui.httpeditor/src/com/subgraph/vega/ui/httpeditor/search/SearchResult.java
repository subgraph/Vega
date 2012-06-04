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
package com.subgraph.vega.ui.httpeditor.search;

import java.util.List;

import org.eclipse.jface.text.IRegion;

class SearchResult {
	
	final private List<IRegion> matches;
	
	private int currentIndex;
	
	
	SearchResult(List<IRegion> matches) {
		this.matches = matches;
		this.currentIndex = 0;
	}
	
	int getCurrentIndex() {
		return currentIndex;
	}
	
	int getResultCount() {
		return matches.size();
	}
	
	boolean hasPrevious() {
		return currentIndex > 0;
	}
	
	boolean hasNext() {
		return matches.size() > (currentIndex + 1);
	}
	
	IRegion getFirstMatch() {
		if(matches.isEmpty()) {
			throw new RuntimeException("Trying to get first match on empty search result");
		}
		currentIndex = 0;
		return matches.get(0);
	}
	
	IRegion getNextMatch() {
		if(!hasNext()) {
			throw new RuntimeException("Next search match does not exist");
		}
		currentIndex += 1;
		return matches.get(currentIndex);
	}
	
	IRegion getPreviousMatch() {
		if(!hasPrevious()) {
			throw new RuntimeException("Previous search match does not exist");
		}
		currentIndex -= 1;
		return matches.get(currentIndex);
	}
}
