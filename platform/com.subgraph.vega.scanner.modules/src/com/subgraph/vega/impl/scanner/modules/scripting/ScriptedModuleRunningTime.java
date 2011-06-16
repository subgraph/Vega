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
package com.subgraph.vega.impl.scanner.modules.scripting;

import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;

public class ScriptedModuleRunningTime implements IScannerModuleRunningTime {

	final private String name;
	
	private int invocationCount = 0;
	private int totalMilliseconds = 0;
	private int worstTime = -1;
	private String worstTimeTarget;
	
	public ScriptedModuleRunningTime(String name) {
		this.name = name;
	}
	
	
	@Override
	public synchronized void reset() {
		invocationCount = 0;
		totalMilliseconds = 0;
		worstTime = -1;
	}

	@Override
	public int getInvocationCount() {
		return invocationCount;
	}

	@Override
	public double getAverageTime() {
		if(invocationCount == 0)
			return 0.0f;
		
		return ((double) totalMilliseconds / (double) invocationCount);
	}

	@Override
	public int getTotalTime() {
		return totalMilliseconds;
	}

	@Override
	public int getWorstTime() {
		return worstTime;
	}
	
	void addTimestamp(int ts, String target) {
		synchronized(this) {
			invocationCount += 1;
			totalMilliseconds += ts;
			if(worstTime < 0 || ts > worstTime) {
				worstTime = ts;
				worstTimeTarget = target;
			}
		}
	}
	
	public String toString() {
		return String.format("Invocations: %3d Average: %8.3f ms [worst: %4d ms @(%s) ] for %s", invocationCount, getAverageTime(), worstTime, worstTimeTarget, name);
	}
}
