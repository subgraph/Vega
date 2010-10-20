package com.subgraph.vega.application.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.console.IConsoleDisplay;

public class ConsoleHandler implements IConsoleDisplay {
	
	private final MessageConsole console;
	private final MessageConsoleStream outputStream;
	private final MessageConsoleStream errorStream;
	
	public ConsoleHandler(Display display, IConsole consoleService) {
		this.console = createMessageConsole();
		this.outputStream = console.newMessageStream();
		this.errorStream = console.newMessageStream();
		this.errorStream.setColor(display.getSystemColor(SWT.COLOR_RED));
		consoleService.registerDisplay(this);
	}
	
	public void activate() {
		
	}
	static private MessageConsole createMessageConsole() {
		MessageConsole console = new MessageConsole("Vega Log", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new org.eclipse.ui.console.IConsole[] {console});
		return console;
	}

	@Override
	public void printOutput(String output) {
		outputStream.print(output);		
	}

	@Override
	public void printError(String output) {
		errorStream.print(output);		
	}

}
