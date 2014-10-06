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
package com.subgraph.vega.application.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.subgraph.vega.api.console.IConsole;

public class LogHandler extends Handler {

	private final IConsole console;
	
	public LogHandler(IConsole console) {
		this.console = console;
	}
	
	@Override
	public void publish(LogRecord record) {
		String message = getFormatter().format(record);
		if(record.getLevel().intValue() > Level.INFO.intValue()) {
			console.error(message);
		} else if (record.getLevel().intValue() == Level.FINER.intValue()) {
			console.debug(message);
		} else {
			console.write(message);
		}
	}

	@Override
	public void flush() {		
	}

	@Override
	public void close() throws SecurityException {		
	}

}
