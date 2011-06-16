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
