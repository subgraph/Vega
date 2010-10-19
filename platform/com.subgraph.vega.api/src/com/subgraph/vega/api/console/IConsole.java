package com.subgraph.vega.api.console;

public interface IConsole {
	void write(String output);
	void error(String output);
	void registerDisplay(IConsoleDisplay display);
}
