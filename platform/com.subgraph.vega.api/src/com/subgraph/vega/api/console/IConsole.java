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

import com.subgraph.vega.api.events.IEventHandler;

public interface IConsole {
	void write(String output);
	void error(String output);
	void registerDisplay(IConsoleDisplay display);
	void addConsoleOutputListener(IEventHandler listener);
	void removeConsoleOutputListener(IEventHandler listener);
	void debug(String output); 
}
