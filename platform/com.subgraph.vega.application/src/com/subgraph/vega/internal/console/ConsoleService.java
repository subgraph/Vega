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
package com.subgraph.vega.internal.console;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.console.ConsoleOutputEvent;
import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.console.IConsoleDisplay;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;

public class ConsoleService implements IConsole {
	private final static int MAX_BUFFER = 8192;
	private final List<IConsoleDisplay> displays = new ArrayList<IConsoleDisplay>();
	private final EventListenerManager eventManager = new EventListenerManager();
	private StringBuilder outputBuffer = null;
	private StringBuilder errorBuffer = null;
	
	@Override
	public synchronized void write(String output) {
		if(output == null || output.isEmpty()) {
			return;
		}
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferOutput(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printOutput(output);
			}
			eventManager.fireEvent(new ConsoleOutputEvent(output, false));
		}
	}

	@Override
	public synchronized void debug(String output) {
		if(output == null || output.isEmpty()) {
			return;
		}
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferOutput(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printDebug(output);
			}
			eventManager.fireEvent(new ConsoleOutputEvent(output, false));
		}
	}
	
	@Override
	public synchronized void error(String output) {
		if(output == null || output.isEmpty()) {
			return;
		}
		if(!output.endsWith("\n"))
			output = output + "\n";
		if(displays.size() == 0) {
			bufferError(output);
		} else {
			for(IConsoleDisplay display: displays) {
				display.printError(output);
			}
			eventManager.fireEvent(new ConsoleOutputEvent(output, true));
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
				eventManager.fireEvent(new ConsoleOutputEvent(errorBuffer.toString(), true));
				errorBuffer = null;
			}
			if(outputBuffer != null) {
				display.printOutput(outputBuffer.toString());
				eventManager.fireEvent(new ConsoleOutputEvent(outputBuffer.toString(), false));
				outputBuffer = null;
			}
		}		
	}

	@Override
	public void addConsoleOutputListener(IEventHandler listener) {
		eventManager.addListener(listener);
	}

	@Override
	public void removeConsoleOutputListener(IEventHandler listener) {
		eventManager.removeListener(listener);
	}
}
