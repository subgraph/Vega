package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.List;

public class PathState404 {
	private final static int MAX_404_SIGS = 4;
	private final PathState pathState;
	private final List<PageFingerprint> page404Fingerprints = new ArrayList<PageFingerprint>();
	private boolean skip404;
	
	PathState404(PathState ps) {
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
	
	public synchronized boolean add404Fingerprint(PageFingerprint fp) {
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
	
	public PathState get404Parent() {
		final PathState parent = pathState.getParentState();
		if(parent != null && parent.has404Fingerprints())
			return parent;
		else
			return null;	
	}
	
	public synchronized boolean has404FingerprintMatching(PageFingerprint fp) {
		if(fp == null)
			return false;
		
		for(PageFingerprint f: page404Fingerprints)
			if(f.isSame(fp))
				return true;
		return false;
	}
	
	public boolean hasParent404Fingerprint(PageFingerprint fp) {
		final PathState pps = get404Parent();
		return (pps != null && pps.has404FingerprintMatching(fp));
	}
	
	public synchronized void clear404Fingerprints() {
		page404Fingerprints.clear();
	}
	
	public void dumpFingerprints() {
		for(PageFingerprint fp: page404Fingerprints) {
			System.out.println(fp);
		}
	}

}
