package com.subgraph.vega.impl.scanner;

import com.subgraph.vega.impl.scanner.state.PathState;

public class ScanRequestData {
	private final int flag;
	private final PathState pathState;
	
	public ScanRequestData(PathState pathState, int flag) {
		this.pathState = pathState;
		this.flag = flag;
	}
	
	public ScanRequestData(PathState pathState) {
		this.pathState = pathState;
		this.flag = 0;
	}
	
	public int getFlag() {
		return flag;
	}
	
	public PathState getPathState() {
		return pathState;
	}
}
