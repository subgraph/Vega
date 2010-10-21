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
