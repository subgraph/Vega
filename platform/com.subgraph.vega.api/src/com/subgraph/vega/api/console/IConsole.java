package com.subgraph.vega.api.console;

import com.subgraph.vega.api.events.IEventHandler;

public interface IConsole {
	void write(String output);
	void error(String output);
	void registerDisplay(IConsoleDisplay display);
	void addConsoleOutputListener(IEventHandler listener);
	void removeConsoleOutputListener(IEventHandler listener);
}
