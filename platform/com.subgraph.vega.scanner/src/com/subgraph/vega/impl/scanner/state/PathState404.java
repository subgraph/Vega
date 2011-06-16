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
package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.http.requests.IPageFingerprint;
import com.subgraph.vega.api.scanner.IPathState;

public class PathState404 {
	private final static int MAX_404_SIGS = 4;
	private final IPathState pathState;
	private final List<IPageFingerprint> page404Fingerprints = new ArrayList<IPageFingerprint>();
	private boolean skip404;
	
	PathState404(IPathState ps) {
		this.pathState = ps;
	}
	
	void setSkip404() {
		skip404 = true;
	}
	
	boolean getSkip404() {
		return skip404;
	}
	
	public synchronized boolean hasMaximum404Fingerprints() {
		return page404Fingerprints.size() == MAX_404_SIGS;
	}
	
	public synchronized boolean add404Fingerprint(IPageFingerprint fp) {
		if(hasMaximum404Fingerprints()) 
			return false;
		if(!has404FingerprintMatching(fp))
			page404Fingerprints.add(fp);
		// XXX add parent check from dir_404_callback
		return true;
	}
	
	public synchronized boolean has404Fingerprints() {
		return !page404Fingerprints.isEmpty();
	}
	
	public IPathState get404Parent() {
		final IPathState parent = pathState.getParentState();
		if(parent != null && parent.has404Fingerprints())
			return parent;
		else
			return null;	
	}
	
	public synchronized boolean has404FingerprintMatching(IPageFingerprint fp) {
		if(fp == null)
			return false;
		
		for(IPageFingerprint f: page404Fingerprints)
			if(f.isSame(fp))
				return true;
		return false;
	}
	
	public boolean hasParent404Fingerprint(IPageFingerprint fp) {
		final IPathState pps = get404Parent();
		return (pps != null && pps.has404FingerprintMatching(fp));
	}
	
	public synchronized void clear404Fingerprints() {
		page404Fingerprints.clear();
	}
	
	public void dumpFingerprints() {
		for(IPageFingerprint fp: page404Fingerprints) {
			System.out.println(fp);
		}
	}

}
