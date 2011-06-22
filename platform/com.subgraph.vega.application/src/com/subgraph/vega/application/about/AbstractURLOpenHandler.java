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
package com.subgraph.vega.application.about;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.handlers.HandlerUtil;

public class AbstractURLOpenHandler extends AbstractHandler {
	private final Logger logger = Logger.getLogger("vega");
	private final String urlString;
	
	protected AbstractURLOpenHandler(String urlString) {
		this.urlString = urlString;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbench workbench = HandlerUtil.getActiveWorkbenchWindow(event).getWorkbench();
		openUrl(workbench);
		return null;
	}
	
	private void openUrl(IWorkbench workbench) {
		try {
			final IWebBrowser browser = workbench.getBrowserSupport().getExternalBrowser();
			final URL url = new URL(urlString);
			browser.openURL(url);
		} catch (PartInitException e) {
			logger.warning("Failed to create browser: "+ e.getMessage());
		} catch (MalformedURLException e) {
			logger.warning("Failed to parse URL string "+ urlString);
		}
	}
}
