package com.subgraph.vega.application;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.application.console.ConsoleHandler;
import com.subgraph.vega.application.logging.LogFormatter;
import com.subgraph.vega.application.logging.LogHandler;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	
	public Object start(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		setupLogging();
		if(!setupWorkspace()) {
			return IApplication.EXIT_OK;
		}
		ConsoleHandler consoleHandler = new ConsoleHandler(display, Activator.getDefault().getConsole());
		consoleHandler.activate();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	private void setupLogging() {
		Logger rootLogger = Logger.getLogger("");

		IConsole console = Activator.getDefault().getConsole();
		if(console != null) {
			Handler handler = new LogHandler(console);
			handler.setFormatter(new LogFormatter());
			handler.setLevel(Level.FINEST);
			for(Handler h: rootLogger.getHandlers())
				rootLogger.removeHandler(h);
			rootLogger.addHandler(handler);
		}
		
		rootLogger.setLevel(Level.WARNING);
	}
	
	private boolean setupWorkspace() {
		final IModel model = Activator.getDefault().getModel();
		if(model == null) {
			MessageDialog.openError(null, "Initialization Error", "Failed to obtain model service");
			return false;
		}
		if(!model.openDefaultWorkspace()) {
			MessageDialog.openError(null, "Initialization Error", "Could not open workspace");
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
