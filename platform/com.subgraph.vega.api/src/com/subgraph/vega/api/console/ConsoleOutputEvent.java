package com.subgraph.vega.api.console;

import com.subgraph.vega.api.events.IEvent;

public class ConsoleOutputEvent implements IEvent {
	
	private final String output;
	private final boolean isErrorOutput;
	
	public ConsoleOutputEvent(String output, boolean isErrorOutput) {
		this.output = output;
		this.isErrorOutput = isErrorOutput;
	}
	
	public String getOutput() {
		return output;
	}

	public boolean isErrorOutput() {
		return isErrorOutput;
	}
}
