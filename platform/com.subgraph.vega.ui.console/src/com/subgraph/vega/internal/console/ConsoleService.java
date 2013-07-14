package com.subgraph.vega.internal.console;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.console.IConsoleDisplay;
import com.subgraph.vega.api.events.IEventHandler;

public class ConsoleService implements IConsole {
	private final static int MAX_BUFFER = 8192;
	private final List<IConsoleDisplay> displays = new ArrayList<IConsoleDisplay>();
	private StringBuilder outputBuffer = null;
	private StringBuilder errorBuffer = null;
	
	@Override
	public synchronized void write(String output) {
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferOutput(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printOutput(output);
			}
		}
	}
	@Override
	public synchronized void debug(String output) {
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferOutput(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printDebug(output);
			}
		}
	}
	@Override
	public synchronized void error(String output) {
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferError(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printError(output);
			}
		}		
	}
	
	private void bufferOutput(String output) {
		if(outputBuffer == null)
			outputBuffer = new StringBuilder();
		appendBuffer(output, outputBuffer);
	}

	private void bufferError(String output) {
		if(errorBuffer == null)
			errorBuffer = new StringBuilder();
		appendBuffer(output, errorBuffer);
	}
	
	private void appendBuffer(String output, StringBuilder buffer) {
		if(output == null)
			return;
		if(output.length() > MAX_BUFFER)
			output = output.substring(0, MAX_BUFFER);
		final int totalLength = buffer.length() + output.length();
		if(totalLength > MAX_BUFFER) {
			int trimCount = totalLength - MAX_BUFFER;
			buffer.delete(0, trimCount);
		}
		buffer.append(output);
	}
	
	@Override
	public synchronized void registerDisplay(IConsoleDisplay display) {
		displays.add(display);
		if(displays.size() == 1) {
			if(errorBuffer != null) {
				display.printError(errorBuffer.toString());
				errorBuffer = null;
			}
			if(outputBuffer != null) {
				display.printOutput(outputBuffer.toString());
				outputBuffer = null;
			}
			
		}		
	}
	@Override
	public void addConsoleOutputListener(IEventHandler listener) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeConsoleOutputListener(IEventHandler listener) {
		// TODO Auto-generated method stub
		
	}
}
