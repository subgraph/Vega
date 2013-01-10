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
package com.subgraph.vega.application.console;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.console.ConsoleView;

import com.subgraph.vega.api.console.ConsoleOutputEvent;
import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.application.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

@SuppressWarnings("restriction")
public class VegaConsoleView extends ConsoleView implements IEventHandler {
	public final static String ID = "com.subgraph.vega.views.VegaConsole";
	private final static int BLINK_INTERVAL = 500;
	private enum ConsoleNotifyLevel { NOTIFY_OFF, NOTIFY_OUTPUT, NOTIFY_ERROR };
	
	private final static String CONSOLE_ICON = "icons/console.png";
	private final static String CONSOLE_ERROR = "icons/console_error.png";
	private final static String CONSOLE_NOTIFY = "icons/console_lightbulb.png";

	private final Timer consoleBlinkTimer = new Timer();
	private TimerTask consoleBlinkTask;
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private IPartListener2 partListener;
	private boolean isVisible;

	private ConsoleNotifyLevel notifyLevel = ConsoleNotifyLevel.NOTIFY_OFF;

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		final IConsole console = Activator.getDefault().getConsole();
		if(console != null) {
			console.addConsoleOutputListener(this);
		}
		partListener = createPartListener();
		getSite().getPage().addPartListener(partListener);
	}

	private IPartListener2 createPartListener() {
		return new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				if(ID.equals(partRef.getId())) {
					isVisible = true;
					setNotifyOff();
				}
			}
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				if(ID.equals(partRef.getId())) {
					isVisible = false;
				}
			}

			@Override public void partOpened(IWorkbenchPartReference partRef) {}
			@Override public void partInputChanged(IWorkbenchPartReference partRef) {}
			@Override public void partDeactivated(IWorkbenchPartReference partRef) {}
			@Override public void partClosed(IWorkbenchPartReference partRef) {}
			@Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}
			@Override public void partActivated(IWorkbenchPartReference partRef) {}
		};
	}

	@Override
	public void dispose() {
		final IConsole console = Activator.getDefault().getConsole();
		if(console != null) {
			console.removeConsoleOutputListener(this);
		}
		getSite().getPage().removePartListener(partListener);
		imageCache.dispose();
		super.dispose();
	}

	public void startErrorNotify() {
		if(consoleBlinkTask == null) {
			consoleBlinkTask = createAlertTask();
			consoleBlinkTimer.scheduleAtFixedRate(consoleBlinkTask, 0, BLINK_INTERVAL);
		}
	}
	
	public void stopErrorNotify() {
		if(consoleBlinkTask != null) {
			consoleBlinkTask.cancel();
			consoleBlinkTask = null;
		}
	}

	private TimerTask createAlertTask() {
		return new TimerTask() {
			private boolean state;

			@Override
			public void run() {
				state = !state;
				if(state) {
					setLabelImage(imageCache.get(CONSOLE_ERROR));
				} else {
					setLabelImage(imageCache.get(CONSOLE_ICON));					
				}
			}
		};
	}

	private synchronized void setNotifyOff() {
		if(notifyLevel == ConsoleNotifyLevel.NOTIFY_ERROR) {
			stopErrorNotify();
		}
		setLabelImage(imageCache.get(CONSOLE_ICON));
		notifyLevel = ConsoleNotifyLevel.NOTIFY_OFF;
	}

	private synchronized void setNotifyOutput() {
		if(notifyLevel == ConsoleNotifyLevel.NOTIFY_OFF) {
			setLabelImage(imageCache.get(CONSOLE_NOTIFY));
			notifyLevel = ConsoleNotifyLevel.NOTIFY_OUTPUT;
		}
	}

	private synchronized void setNotifyError() {
		if(notifyLevel != ConsoleNotifyLevel.NOTIFY_ERROR) {
			startErrorNotify();
			notifyLevel = ConsoleNotifyLevel.NOTIFY_ERROR;
		}
	}

	private void setLabelImage(final Image image) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(image != null && !image.isDisposed()) {
					setTitleImage(image);
				}
			}
		});
	}

	@Override
	public void handleEvent(IEvent event) {
		if(event instanceof ConsoleOutputEvent) {
			handleConsoleOutput((ConsoleOutputEvent) event);
		}
	}
	
	private void handleConsoleOutput(ConsoleOutputEvent event) {
		if(!isVisible) {
			if(event.isErrorOutput()) {
				setNotifyError();
			} else {
				setNotifyOutput();
			}
		}
	}
}
