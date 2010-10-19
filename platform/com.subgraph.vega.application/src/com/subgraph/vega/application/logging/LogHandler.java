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
		if(record.getLevel().intValue() > Level.INFO.intValue())
			console.error(message);
		else
			console.write(message);		
	}

	@Override
	public void flush() {		
	}

	@Override
	public void close() throws SecurityException {		
	}

}
