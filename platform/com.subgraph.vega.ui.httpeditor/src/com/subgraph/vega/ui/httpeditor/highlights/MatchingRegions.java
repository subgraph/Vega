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
package com.subgraph.vega.ui.httpeditor.highlights;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.text.IRegion;

public class MatchingRegions {

	private final String annotationType;
	private final SortedSet<IRegion> regionsMatched;
	private final List<RegionInfo> regionInfo;
	private int currentIndex;
	private boolean isLocked;
	
	public MatchingRegions(String annotationType) {
		this.annotationType = annotationType;
		this.regionsMatched = new TreeSet<IRegion>(createRegionComparator());
		this.regionInfo = new ArrayList<RegionInfo>();
		reset();
	}
	
	private Comparator<IRegion> createRegionComparator() {
		return new Comparator<IRegion>() {
			@Override
			public int compare(IRegion r1, IRegion r2) {
				if(r1.getOffset() == r2.getOffset() && r1.getLength() == r2.getLength()) {
					return 0;
				}
				// If offsets are the same, the shorter region is 'lower'
				if(r1.getOffset() == r2.getOffset()) {
					if(r1.getLength() < r2.getLength()) {
						return -1;
					} else {
						return 1;
					}
				}
				// Otherwise the lower region is the one that begins at the lower offset
				if(r1.getOffset() < r2.getOffset()) {
					return -1;
				} else {
					return 1;
				}
			}
		};
	}
	
	public void reset() {
		regionsMatched.clear();
		regionInfo.clear();
		currentIndex = 0;
		isLocked = false;
	}

	public List<RegionInfo> getAllRegions() {
		lockRegions();
		return Collections.unmodifiableList(regionInfo);
	}
	
	private void lockRegions() {
		if(isLocked) {
			return;
		}
		isLocked = true;
		for(IRegion r: regionsMatched) {
			regionInfo.add(new RegionInfo(r, annotationType));
		}
	}

	public void addRegions(List<IRegion> matches) {
		if(isLocked) {
			throw new IllegalStateException("Cannot add items to locked matching regions");
		}
		regionsMatched.addAll(matches);
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public int getMatchCount() {
		lockRegions();
		return regionInfo.size();
	}
	
	public boolean hasPrevious() {
		lockRegions();
		return currentIndex > 0;
	}
	
	public boolean hasNext() {
		lockRegions();
		return regionInfo.size() > (currentIndex + 1);
	}
	
	public RegionInfo getFirstRegion() {
		lockRegions();
		if(regionInfo.isEmpty()) {
			throw new RuntimeException("Trying to get first match on empty set");
		}
		currentIndex = 0;
		return regionInfo.get(0);
	}
	
	public RegionInfo getNextRegion() {
		lockRegions();
		if(!hasNext()) {
			throw new RuntimeException("No next region");
		}
		currentIndex += 1;
		return regionInfo.get(currentIndex);
	}
	
	public RegionInfo getPreviousRegion() {
		lockRegions();
		if(!hasPrevious()) {
			throw new RuntimeException("No previous highlight");
		}
		currentIndex -= 1;
		return regionInfo.get(currentIndex);
	}
}
