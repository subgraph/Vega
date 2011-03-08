package com.subgraph.vega.api.scanner.modules;

public interface IScannerModuleRunningTime {
	void reset();
	int getInvocationCount();
	double getAverageTime();
	int getTotalTime();
	int getWorstTime();
}
