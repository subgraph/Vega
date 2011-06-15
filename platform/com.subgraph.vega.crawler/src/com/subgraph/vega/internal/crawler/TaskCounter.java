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
package com.subgraph.vega.internal.crawler;

public class TaskCounter {
	private int totalTasks;
	private int completedTasks;
	
	void addNewTask() {
		totalTasks += 1;
	}
	
	void addCompletedTask() {
		completedTasks += 1;
	}
	
	int getTotalTasks() {
		return totalTasks;
	}
	
	int getCompletedTasks() {
		return completedTasks;
	}

}
