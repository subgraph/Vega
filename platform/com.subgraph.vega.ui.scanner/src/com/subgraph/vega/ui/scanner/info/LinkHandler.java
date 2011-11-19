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
package com.subgraph.vega.ui.scanner.info;

import java.util.logging.Logger;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.services.IServiceLocator;

import com.subgraph.vega.ui.http.request.view.HttpRequestView;
import com.subgraph.vega.ui.scanner.ScannerPerspectiveFactory;

public class LinkHandler extends BrowserFunction {
	final private static Logger logger = Logger.getLogger("alertView");
	
	private final IServiceLocator serviceLocator;
	
	public LinkHandler(Browser browser, IServiceLocator serviceLocator) {
		super(browser, "linkClick");
		this.serviceLocator = serviceLocator;
	}
	
	@Override
	public Object function (Object[] arguments) {
		try {
			IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class);
			ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
			Command showView = commandService.getCommand(IWorkbenchCommandConstants.VIEWS_SHOW_VIEW);
			IParameter parm1 = showView.getParameter(IWorkbenchCommandConstants.VIEWS_SHOW_VIEW_PARM_ID);
			Parameterization parmId1 = new Parameterization(parm1, HttpRequestView.ID);
			IParameter parm2 = showView.getParameter(IWorkbenchCommandConstants.VIEWS_SHOW_VIEW_SECONDARY_ID);
			Parameterization parmId2 = new Parameterization(parm2, ScannerPerspectiveFactory.HTTP_VIEW_SECONDARY_ID);
			ParameterizedCommand parmCommand = new ParameterizedCommand(showView, new Parameterization[] { parmId1, parmId2 });
			handlerService.executeCommand(parmCommand, null);

			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(HttpRequestView.ID, ScannerPerspectiveFactory.HTTP_VIEW_SECONDARY_ID, IWorkbenchPage.VIEW_VISIBLE);
			if(view instanceof HttpRequestView && arguments[0] instanceof String) {
				final HttpRequestView requestView = (HttpRequestView) view;
				try {
					long id = Long.parseLong((String) arguments[0]);
					requestView.focusOnRecord(id);
				} catch (NumberFormatException e) {
					
				}
			}
		} catch (PartInitException e) {
			logger.warning("Failed to open HTTP request viewer");
		} catch (NotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotHandledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
